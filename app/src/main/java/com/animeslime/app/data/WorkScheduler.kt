package com.animeslime.app.data
import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit
object WorkScheduler{fun schedule(context:Context){val request=PeriodicWorkRequestBuilder<RepositoryRefreshWorker>(12,TimeUnit.HOURS).setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()).build();WorkManager.getInstance(context).enqueueUniquePeriodicWork("repository-refresh",ExistingPeriodicWorkPolicy.UPDATE,request)}}
