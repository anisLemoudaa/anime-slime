package com.animeslime.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

data class SourceEpisode(val url:String,val number:Double,val title:String,val videoLinks:List<String>)
data class SourceDetails(val anime:SourceAnime,val episodes:List<SourceEpisode>,val genres:List<String>,val status:String?)
interface CatalogSource { val id:String; val name:String; suspend fun latest():Result<List<SourceAnime>>; suspend fun search(query:String):Result<List<SourceAnime>>; suspend fun details(url:String):Result<SourceDetails> }
class AnimeLekCatalog:CatalogSource {
 override val id="animelek"; override val name="Animelek"; private val base="https://animelek.cc/"; private val http=OkHttpClient.Builder().connectTimeout(15,TimeUnit.SECONDS).readTimeout(25,TimeUnit.SECONDS).build()
 override suspend fun latest()=page(base+"home/")
 override suspend fun search(query:String)=page(base+"?s="+URLEncoder.encode(query,"UTF-8"))
 private suspend fun page(url:String):Result<List<SourceAnime>> =withContext(Dispatchers.IO){runCatching{val html=get(url);Regex("<a[^>]+href=\\\"([^\\\"]+)\\\"[^>]*>(.*?)</a>",RegexOption.IGNORE_CASE).findAll(html).mapNotNull{m->val title=clean(m.groupValues[2]);if(title.length<2)null else SourceAnime(m.groupValues[1],title)}.distinctBy{it.url}.take(100).toList()}}
 override suspend fun details(url:String):Result<SourceDetails> = withContext(Dispatchers.IO){runCatching{val html=get(url);val eps=Regex("(?:episode|ep|حلقة)[^\\d]{0,20}(\\d+(?:\\.\\d+)?)",RegexOption.IGNORE_CASE).findAll(html).map{m->SourceEpisode(url+"#episode-${m.groupValues[1]}",m.groupValues[1].toDouble(),"الحلقة ${m.groupValues[1]}",extractVideos(html))}.distinctBy{it.number}.toList();SourceDetails(SourceAnime(url,clean(Regex("<title>(.*?)</title>",RegexOption.IGNORE_CASE).find(html)?.groupValues?.get(1).orEmpty())),eps,emptyList(),null)}}
 private fun get(url:String):String{val u=if(url.startsWith("http"))url else base+url.trimStart('/');return http.newCall(Request.Builder().url(u).header("User-Agent","AnimeSlime/1.0").build()).execute().use{r->require(r.isSuccessful){"HTTP ${r.code}"};r.body?.string().orEmpty()}}
 private fun clean(s:String)=s.replace(Regex("<[^>]+>")," ").replace(Regex("\\s+")," ").trim()
 private fun extractVideos(html:String)=Regex("(?:data-video|data-src|src)=\\\"(https?://[^\\\"]+)\\\"",RegexOption.IGNORE_CASE).findAll(html).map{it.groupValues[1]}.distinct().toList()
}
