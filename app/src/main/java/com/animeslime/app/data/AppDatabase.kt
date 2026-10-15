package com.animeslime.app.data
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities=[Anime::class,Episode::class,Category::class,AnimeCategory::class,History::class,DownloadItem::class,Repository::class,Extension::class,TrackerLink::class],version=2,exportSchema=true)
abstract class AppDatabase:RoomDatabase(){abstract fun animeDao():AnimeDao;abstract fun episodeDao():EpisodeDao;abstract fun repositoryDao():RepositoryDao;abstract fun extensionDao():ExtensionDao;abstract fun historyDao():HistoryDao;abstract fun downloadDao():DownloadDao}
