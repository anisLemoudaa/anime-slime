package com.animeslime.app

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import androidx.room.Room
import com.animeslime.app.data.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@HiltAndroidApp class AnimeSlimeApplication:Application(){
 override fun onCreate(){super.onCreate();AnimeNotifications.create(this);WorkScheduler.schedule(this);val db=Room.databaseBuilder(this,AppDatabase::class.java,"anime_slime.db").fallbackToDestructiveMigration().build();CoroutineScope(Dispatchers.IO).launch{val repos=db.repositoryDao().all().first();if(repos.none{it.url=="https://raw.githubusercontent.com/yuzono/anime-repo/repo/index.min.json"})db.repositoryDao().upsert(Repository(name="Anime Repo",url="https://raw.githubusercontent.com/yuzono/anime-repo/repo/index.min.json",trusted=true));if(repos.none{it.url=="https://animelek.cc/home/"})db.repositoryDao().upsert(Repository(name="Animelek",url="https://animelek.cc/home/",trusted=false))}}
}
@Module @InstallIn(SingletonComponent::class) object AppModule{
 @Provides @Singleton fun db(app:Application):AppDatabase=Room.databaseBuilder(app,AppDatabase::class.java,"anime_slime.db").fallbackToDestructiveMigration().build()
 @Provides @Singleton fun prefs(app:Application)=AppPreferences(app)
 @Provides fun animeDao(db:AppDatabase)=db.animeDao(); @Provides fun episodeDao(db:AppDatabase)=db.episodeDao(); @Provides fun repoDao(db:AppDatabase)=db.repositoryDao(); @Provides fun historyDao(db:AppDatabase)=db.historyDao(); @Provides fun downloadDao(db:AppDatabase)=db.downloadDao()
 @Provides @Singleton fun repositoryService()=RepositoryService()
 @Provides @Singleton fun animeLekSource()=AnimeLekSource()
 @Provides @Singleton fun animeLekCatalog()=AnimeLekCatalog()
 @Provides @Singleton fun oauthManager(app:Application)=OAuthManager(app)
}
