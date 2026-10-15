package com.animeslime.app.data
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao interface AnimeDao {
 @Query("SELECT * FROM Anime WHERE inLibrary=1 ORDER BY lastUpdate DESC") fun library():Flow<List<Anime>>
 @Query("SELECT * FROM Anime WHERE title LIKE '%' || :q || '%' ORDER BY title LIMIT 100") fun search(q:String):Flow<List<Anime>>
 @Query("SELECT * FROM Anime WHERE id=:id LIMIT 1") fun byId(id:Long):Flow<Anime?>
 @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun upsert(anime:Anime):Long
 @Query("UPDATE Anime SET inLibrary=:value,lastUpdate=:time WHERE id=:id") suspend fun setLibrary(id:Long,value:Boolean,time:Long=System.currentTimeMillis())
 @Query("DELETE FROM Anime WHERE id=:id") suspend fun delete(id:Long)
}
@Dao interface EpisodeDao {
 @Query("SELECT * FROM Episode WHERE animeId=:animeId ORDER BY number ASC") fun forAnime(animeId:Long):Flow<List<Episode>>
 @Query("SELECT * FROM Episode WHERE id=:id LIMIT 1") suspend fun byId(id:Long):Episode?
 @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun upsertAll(episodes:List<Episode>)
 @Query("UPDATE Episode SET seen=:seen,progressMs=:progress,durationMs=:duration WHERE id=:id") suspend fun saveProgress(id:Long,seen:Boolean,progress:Long,duration:Long)
 @Query("UPDATE Episode SET downloaded=:value WHERE id=:id") suspend fun setDownloaded(id:Long,value:Boolean)
}
@Dao interface RepositoryDao {
 @Query("SELECT * FROM Repository ORDER BY enabled DESC,lastFetched DESC") fun all():Flow<List<Repository>>
 @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun upsert(repo:Repository):Long
 @Query("DELETE FROM Repository WHERE id=:id") suspend fun delete(id:Long)
 @Query("UPDATE Repository SET enabled=:enabled WHERE id=:id") suspend fun setEnabled(id:Long,enabled:Boolean)
}
@Dao interface HistoryDao { @Query("SELECT * FROM History ORDER BY watchedAt DESC LIMIT 100") fun recent():Flow<List<History>>; @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun save(item:History); @Query("DELETE FROM History") suspend fun clear() }
@Dao interface DownloadDao { @Query("SELECT * FROM DownloadItem ORDER BY rowid") fun all():Flow<List<DownloadItem>>; @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun upsert(item:DownloadItem); @Query("UPDATE DownloadItem SET state=:state,progress=:progress,error=:error WHERE episodeId=:id") suspend fun update(id:Long,state:String,progress:Int,error:String?=null) }
