package com.animeslime.app.data
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao interface AnimeDao {
 @Query("SELECT * FROM Anime WHERE inLibrary=1 ORDER BY lastUpdate DESC") fun library():Flow<List<Anime>>
 @Query("SELECT * FROM Anime WHERE title LIKE '%' || :q || '%' ORDER BY title LIMIT 100") fun search(q:String):Flow<List<Anime>>
 @Query("SELECT * FROM Anime WHERE id=:id LIMIT 1") fun byId(id:Long):Flow<Anime?>
 @Query("SELECT * FROM Anime WHERE sourceId=:sourceId AND url=:url LIMIT 1") suspend fun bySourceAndUrl(sourceId:String,url:String):Anime?
 @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun upsert(anime:Anime):Long
 @Query("UPDATE Anime SET inLibrary=:value,lastUpdate=:time WHERE id=:id") suspend fun setLibrary(id:Long,value:Boolean,time:Long=System.currentTimeMillis())
 @Query("DELETE FROM Anime WHERE id=:id") suspend fun delete(id:Long)
}
data class DownloadRow(val episodeId:Long,val url:String,val state:String,val progress:Int,val filePath:String?,val error:String?,val number:Double,val episodeName:String?,val animeTitle:String)
@Dao interface EpisodeDao {
 @Query("SELECT * FROM Episode WHERE animeId=:animeId ORDER BY number ASC") fun forAnime(animeId:Long):Flow<List<Episode>>
 @Query("SELECT * FROM Episode WHERE id=:id LIMIT 1") suspend fun byId(id:Long):Episode?
 @Query("SELECT * FROM Episode WHERE animeId=:animeId AND number=:number LIMIT 1") suspend fun byAnimeAndNumber(animeId:Long,number:Double):Episode?
 @Query("SELECT * FROM Episode WHERE animeId=:animeId AND url=:url LIMIT 1") suspend fun byAnimeAndUrl(animeId:Long,url:String):Episode?
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
@Dao interface ExtensionDao {
 @Query("SELECT * FROM Extension ORDER BY installed DESC,name") fun all():Flow<List<Extension>>
 @Query("SELECT * FROM Extension WHERE installed=1 AND enabled=1 ORDER BY name") fun installed():Flow<List<Extension>>
 @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun upsertAll(items:List<Extension>)
 @Query("UPDATE Extension SET installed=:value WHERE pkg=:pkg") suspend fun setInstalled(pkg:String,value:Boolean)
 @Query("UPDATE Extension SET enabled=:value WHERE pkg=:pkg") suspend fun setEnabled(pkg:String,value:Boolean)
 @Query("DELETE FROM Extension WHERE pkg=:pkg") suspend fun delete(pkg:String)
}
@Dao interface HistoryDao { @Query("SELECT * FROM History ORDER BY watchedAt DESC LIMIT 100") fun recent():Flow<List<History>>; @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun save(item:History); @Query("DELETE FROM History") suspend fun clear() }
@Dao interface DownloadDao { @Query("SELECT * FROM DownloadItem ORDER BY rowid") fun all():Flow<List<DownloadItem>>; @Query("SELECT d.episodeId,d.url,d.state,d.progress,d.filePath,d.error,e.number,e.name AS episodeName,a.title AS animeTitle FROM DownloadItem d JOIN Episode e ON e.id=d.episodeId JOIN Anime a ON a.id=e.animeId ORDER BY d.episodeId DESC") fun rows():Flow<List<DownloadRow>>; @Query("SELECT * FROM DownloadItem WHERE episodeId=:id LIMIT 1") suspend fun byId(id:Long):DownloadItem?; @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun upsert(item:DownloadItem); @Query("UPDATE DownloadItem SET state=:state,progress=:progress,error=:error,filePath=:path WHERE episodeId=:id") suspend fun update(id:Long,state:String,progress:Int,error:String?=null,path:String?=null); @Query("DELETE FROM DownloadItem WHERE episodeId=:id") suspend fun delete(id:Long) }
