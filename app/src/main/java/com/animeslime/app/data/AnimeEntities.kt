package com.animeslime.app.data

import androidx.room.*

@Entity(indices=[Index(value=["sourceId","url"],unique=true)])
data class Anime(@PrimaryKey(autoGenerate=true) val id:Long=0,val sourceId:String,val url:String,val title:String,val cover:String?=null,val description:String?=null,val genres:String?=null,val status:String?=null,val inLibrary:Boolean=false,val addedAt:Long=System.currentTimeMillis(),val lastUpdate:Long=System.currentTimeMillis())
@Entity(foreignKeys=[ForeignKey(entity=Anime::class,parentColumns=["id"],childColumns=["animeId"],onDelete=ForeignKey.CASCADE)],indices=[Index(value=["animeId","number"],unique=true)])
data class Episode(@PrimaryKey(autoGenerate=true) val id:Long=0,val animeId:Long,val url:String,val number:Double,val name:String?=null,val seen:Boolean=false,val progressMs:Long=0,val durationMs:Long=0,val downloaded:Boolean=false,val uploadedAt:Long=System.currentTimeMillis(),val videoUrl:String?=null)
@Entity data class Category(@PrimaryKey(autoGenerate=true) val id:Long=0,val name:String,val order:Int=0)
@Entity(primaryKeys=["animeId","categoryId"]) data class AnimeCategory(val animeId:Long,val categoryId:Long)
@Entity(foreignKeys=[ForeignKey(entity=Episode::class,parentColumns=["id"],childColumns=["episodeId"],onDelete=ForeignKey.CASCADE)]) data class History(@PrimaryKey val episodeId:Long,val watchedAt:Long=System.currentTimeMillis(),val progressMs:Long=0)
@Entity(foreignKeys=[ForeignKey(entity=Episode::class,parentColumns=["id"],childColumns=["episodeId"],onDelete=ForeignKey.CASCADE)]) data class DownloadItem(@PrimaryKey val episodeId:Long,val state:String="QUEUED",val progress:Int=0,val filePath:String?=null,val error:String?=null)
@Entity data class Repository(@PrimaryKey(autoGenerate=true) val id:Long=0,val name:String,val url:String,val enabled:Boolean=true,val trusted:Boolean=false,val extensionCount:Int=0,val lastFetched:Long?=null,val lastError:String?=null)
@Entity(indices=[Index(value=["pkg","repoId"],unique=true)]) data class Extension(@PrimaryKey val pkg:String,val name:String,val lang:String,val version:String,val repoId:Long,val installed:Boolean=false,val nsfw:Boolean=false)
@Entity(primaryKeys=["animeId","service"]) data class TrackerLink(val animeId:Long,val service:String,val remoteId:String,val status:String?=null,val score:Float?=null,val progress:Int=0)
