package com.animeslime.app.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first

class RepositoryRefreshWorker(context:Context,params:WorkerParameters):CoroutineWorker(context,params){
 override suspend fun doWork():Result=runCatching{val db=androidx.room.Room.databaseBuilder(applicationContext,AppDatabase::class.java,"anime_slime.db").build();val service=RepositoryService();db.repositoryDao().all().first().filter{it.enabled}.forEach{repo->service.fetch(repo.url).onSuccess{idx->db.repositoryDao().upsert(repo.copy(name=idx.name,extensionCount=idx.extensions.size,lastFetched=System.currentTimeMillis(),lastError=null))}.onFailure{e->db.repositoryDao().upsert(repo.copy(lastError=e.message))}};Result.success()}.getOrElse{Result.retry()}
}
