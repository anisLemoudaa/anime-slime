package com.animeslime.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

data class SourceAnime(val url:String,val title:String,val cover:String?=null,val description:String?=null)
class AnimeLekSource{
 val baseUrl="https://animelek.cc/home/"
 private val client=OkHttpClient.Builder().connectTimeout(15,TimeUnit.SECONDS).readTimeout(25,TimeUnit.SECONDS).build()
 suspend fun search(query:String):Result<List<SourceAnime>>=fetch("${baseUrl}?s=${java.net.URLEncoder.encode(query,"UTF-8")}")
 suspend fun latest():Result<List<SourceAnime>>=fetch(baseUrl)
 private suspend fun fetch(url:String):Result<List<SourceAnime>>=withContext(Dispatchers.IO){runCatching{val req=Request.Builder().url(url).header("User-Agent","AnimeSlime/0.2").build();client.newCall(req).execute().use{r->require(r.isSuccessful){"فشل تحميل Animelek: ${r.code}"};parseHtml(r.body?.string().orEmpty())}}}
 private fun parseHtml(html:String):List<SourceAnime>{val out=mutableListOf<SourceAnime>();val regex=Regex("<a[^>]+href=\\\"([^\\\"]+)\\\"[^>]*>(.*?)</a>",RegexOption.IGNORE_CASE);for(m in regex.findAll(html)){val title=m.groupValues[2].replace(Regex("<[^>]+>"),"").trim();if(title.length>1&&!out.any{it.url==m.groupValues[1]})out+=SourceAnime(m.groupValues[1],title)};return out.take(100)}
}
