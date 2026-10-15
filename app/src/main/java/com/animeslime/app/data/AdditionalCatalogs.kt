package com.animeslime.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.jsoup.Jsoup
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

abstract class HttpCatalog:CatalogSource{
 protected val http=OkHttpClient.Builder().connectTimeout(20,TimeUnit.SECONDS).readTimeout(30,TimeUnit.SECONDS).build()
 protected fun document(url:String)=http.newCall(Request.Builder().url(url).header("User-Agent","Mozilla/5.0 (Android) AnimeSlime/1.0").build()).execute().use{r->require(r.isSuccessful){"HTTP ${r.code}"};Jsoup.parse(r.body?.string().orEmpty(),url)}
 protected fun text(value:String?)=value.orEmpty().replace(Regex("\\s+")," ").trim()
}

class WitAnimeCatalog:HttpCatalog(){
 override val id="witanime";override val name="WitAnime";private val base="https://witanime.site"
 override suspend fun latest():Result<List<SourceAnime>> =withContext(Dispatchers.IO){runCatching{parse(document(base+"/"))}}
 override suspend fun search(query:String):Result<List<SourceAnime>> =withContext(Dispatchers.IO){runCatching{parse(document("$base/search?q=${URLEncoder.encode(query,"UTF-8")}"))}}
 private fun parse(doc:org.jsoup.nodes.Document):List<SourceAnime>{return doc.select("a[href^='$base/anime/'],a[href^='/anime/']").mapNotNull{a->val url=a.absUrl("href");val title=text(a.selectFirst("h2,h3")?.text().orEmpty().ifBlank{a.text()});if(url.isBlank()||title.length<2) null else SourceAnime(url,title,a.selectFirst("img")?.absUrl("src"),sourceId=id)}.distinctBy{it.url}.take(100)}
 override suspend fun details(url:String):Result<SourceDetails> = withContext(Dispatchers.IO){runCatching{val doc=document(url);val title=text(doc.selectFirst("h1,h2,title")?.text()).ifBlank{"أنمي"};val eps=doc.select("a[href*='/watch/']").mapNotNull{a->val n=Regex("/(\\d+)$").find(a.absUrl("href"))?.groupValues?.get(1)?.toDoubleOrNull()?:return@mapNotNull null;SourceEpisode(a.absUrl("href"),n,text(a.text()).ifBlank{"الحلقة $n"},emptyList())}.distinctBy{it.number}.sortedBy{it.number};SourceDetails(SourceAnime(url,title,doc.selectFirst("meta[property=og:image]")?.attr("content"),sourceId=id),eps,emptyList(),null)}}
}

class DonghuaCatalog:HttpCatalog(){
 override val id="donghua-ar";override val name="Donghua-Ar";private val base="https://donghua-ar.me";private val api="$base/api/trpc/"
 private fun call(path:String,input:JSONObject=JSONObject()):JSONObject{val encoded=java.net.URLEncoder.encode(JSONObject().put("json",input).toString(),"UTF-8");val request=Request.Builder().url(api+path+"?input="+encoded).header("User-Agent","AnimeSlime/1.0").header("x-trpc-source","client").build();return http.newCall(request).execute().use{r->require(r.isSuccessful){"HTTP ${r.code}"};JSONObject(r.body?.string().orEmpty()).getJSONObject("result").getJSONObject("data").getJSONObject("json")}}
 private fun callArray(path:String,input:JSONObject=JSONObject()):List<JSONObject>{val encoded=java.net.URLEncoder.encode(JSONObject().put("json",input).toString(),"UTF-8");val request=Request.Builder().url(api+path+"?input="+encoded).header("User-Agent","AnimeSlime/1.0").header("x-trpc-source","client").build();return http.newCall(request).execute().use{r->require(r.isSuccessful){"HTTP ${r.code}"};val raw=JSONObject(r.body?.string().orEmpty()).getJSONObject("result").getJSONObject("data").getJSONArray("json");(0 until raw.length()).map{raw.getJSONObject(it)}}}
 override suspend fun latest():Result<List<SourceAnime>> =withContext(Dispatchers.IO){runCatching{callArray("series.latest",JSONObject().put("limit",50)).map(::series).distinctBy{it.url}}}
 override suspend fun search(query:String):Result<List<SourceAnime>> =withContext(Dispatchers.IO){runCatching{callArray("series.list",JSONObject().put("limit",200)).map(::series).filter{it.title.contains(query,true)}}}
 private fun series(o:JSONObject)=SourceAnime("$base/series/${o.optString("slug")}",o.optString("title").ifBlank{o.optString("titleEn")},o.optString("image").ifBlank{null},o.optString("description").ifBlank{null},id)
 override suspend fun details(url:String):Result<SourceDetails> = withContext(Dispatchers.IO){runCatching{val slug=url.substringAfterLast('/');val o=call("series.getBySlug",JSONObject().put("slug",slug));val anime=series(o);val eps=callArray("episodes.listBySeries",JSONObject().put("seriesId",o.getLong("id"))).mapNotNull{e->val n=e.optString("episodeNumber").toDoubleOrNull()?:return@mapNotNull null;SourceEpisode("$base/watch/$slug/${e.optString("episodeNumber")}",n,e.optString("title").ifBlank{"الحلقة $n"},emptyList())};SourceDetails(anime,eps,emptyList(),o.optString("status"))}}
}

class CatalogRegistry(private val catalogs:List<CatalogSource>){
 fun all()=catalogs
 fun byUrl(url:String)=catalogs.firstOrNull{url.contains(it.id)||when(it.id){"witanime"->url.contains("witanime.site");"donghua-ar"->url.contains("donghua-ar.me");"animelek"->url.contains("animelek.cc");else->false}}?:catalogs.first()
 suspend fun latestAll():List<SourceAnime> = catalogs.flatMap{it.latest().getOrElse{emptyList()}}
 suspend fun searchAll(query:String):List<SourceAnime> = catalogs.flatMap{it.search(query).getOrElse{emptyList()}}
}
