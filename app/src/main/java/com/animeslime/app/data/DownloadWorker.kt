package com.animeslime.app.data

import android.content.Context
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

class DownloadWorker(context:Context,params:WorkerParameters):CoroutineWorker(context,params){
 override suspend fun doWork():Result=withContext(Dispatchers.IO){
  val id=inputData.getLong("episode_id",-1);val url=inputData.getString("url")?:return@withContext Result.failure()
  val db=androidx.room.Room.databaseBuilder(applicationContext,AppDatabase::class.java,"anime_slime.db").fallbackToDestructiveMigration().build();val dao=db.downloadDao();val episodeDao=db.episodeDao()
  val dir=File(applicationContext.getExternalFilesDir(null),"downloads").apply{mkdirs()};val file=File(dir,"episode_$id.mp4")
  try{
   val existing=if(file.exists())file.length() else 0L;dao.update(id,"RUNNING",0,null,file.absolutePath)
   val request=Request.Builder().url(url).apply{if(existing>0)header("Range","bytes=$existing-")}.build()
   OkHttpClient().newCall(request).execute().use{res->
    if(!res.isSuccessful)error("HTTP ${res.code}")
    val body=res.body?:error("empty response");val append=existing>0&&res.code==206;val start=if(append)existing else 0L
    if(!append&&existing>0)file.delete();val total=body.contentLength().let{if(it>0)it+start else -1L};var done=start
    body.byteStream().use{input->FileOutputStream(file,append).use{output->val buffer=ByteArray(64*1024);while(true){ensureActive();val n=input.read(buffer);if(n<0)break;output.write(buffer,0,n);done+=n;if(total>0){val progress=(done*100/total).toInt().coerceIn(0,99);dao.update(id,"RUNNING",progress,null,file.absolutePath);setProgress(workDataOf("progress" to progress,"path" to file.absolutePath))}}}}
   }
   dao.update(id,"COMPLETED",100,null,file.absolutePath);episodeDao.setDownloaded(id,true);Result.success(workDataOf("path" to file.absolutePath))
  }catch(e:Exception){if(isStopped||e is kotlinx.coroutines.CancellationException){dao.update(id,"PAUSED",0,e.message,file.absolutePath);Result.failure()}else{dao.update(id,"FAILED",0,e.message,file.absolutePath);if(runAttemptCount<2)Result.retry() else Result.failure()}}
  finally{db.close()}
 }
}
object DownloadQueue{
 fun enqueue(context:Context,episodeId:Long,url:String){val work=OneTimeWorkRequestBuilder<DownloadWorker>().setInputData(workDataOf("episode_id" to episodeId,"url" to url)).setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()).build();WorkManager.getInstance(context).enqueueUniqueWork("episode-$episodeId",ExistingWorkPolicy.REPLACE,work)}
 fun cancel(context:Context,episodeId:Long){WorkManager.getInstance(context).cancelUniqueWork("episode-$episodeId")}
}
