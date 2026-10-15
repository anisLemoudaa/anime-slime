package com.animeslime.app.data

import android.content.Context
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.ensureActive
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

class DownloadWorker(context:Context,params:WorkerParameters):CoroutineWorker(context,params){
 override suspend fun doWork():Result=withContext(Dispatchers.IO){val id=inputData.getLong("episode_id",-1);val url=inputData.getString("url")?:return@withContext Result.failure();val db=androidx.room.Room.databaseBuilder(applicationContext,AppDatabase::class.java,"anime_slime.db").build();val dao=db.downloadDao();try{dao.update(id,"RUNNING",0);val dir=File(applicationContext.getExternalFilesDir(null),"downloads").apply{mkdirs()};val file=File(dir,"episode_$id.mp4");val client=OkHttpClient();client.newCall(Request.Builder().url(url).build()).execute().use{res->if(!res.isSuccessful)error("HTTP ${res.code}");val body=res.body?:error("empty response");val total=body.contentLength();body.byteStream().use{input->file.outputStream().use{output->val buffer=ByteArray(8192);var done=0L;while(true){ensureActive();val n=input.read(buffer);if(n<0)break;output.write(buffer,0,n);done+=n;if(total>0)dao.update(id,"RUNNING",(done*100/total).toInt())}}}};dao.update(id,"COMPLETED",100);Result.success(workDataOf("path" to file.absolutePath))}catch(e:Exception){dao.update(id,"FAILED",0,e.message);if(runAttemptCount<2)Result.retry() else Result.failure()}}
}
object DownloadQueue{fun enqueue(context:Context,episodeId:Long,url:String){val work=OneTimeWorkRequestBuilder<DownloadWorker>().setInputData(workDataOf("episode_id" to episodeId,"url" to url)).setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()).build();WorkManager.getInstance(context).enqueueUniqueWork("episode-$episodeId",ExistingWorkPolicy.KEEP,work)}}
