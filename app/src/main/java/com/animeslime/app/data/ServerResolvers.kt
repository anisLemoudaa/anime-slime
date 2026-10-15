package com.animeslime.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.jsoup.Jsoup
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

data class ResolvedServer(val url:String,val label:String,val direct:Boolean)
interface ServerResolver{val id:String;fun canHandle(url:String):Boolean;suspend fun resolve(url:String):Result<List<ResolvedServer>>}
abstract class ResolverHttp{protected val http=OkHttpClient.Builder().connectTimeout(20,TimeUnit.SECONDS).readTimeout(30,TimeUnit.SECONDS).build();protected fun get(url:String,headers:Map<String,String> = emptyMap()):String=http.newCall(Request.Builder().url(url).header("User-Agent","Mozilla/5.0 (Android) AnimeSlime/1.0").apply{headers.forEach{(k,v)->header(k,v)}}.build()).execute().use{r->require(r.isSuccessful){"HTTP ${r.code}"};r.body?.string().orEmpty()}}

class DonghuaServerResolver:ResolverHttp(),ServerResolver{
 override val id="donghua-ar";private val api="https://donghua-ar.me/api/trpc/"
 override fun canHandle(url:String)=url.contains("donghua-ar.me")
 private fun call(path:String,input:JSONObject):JSONObject{val q=URLEncoder.encode(JSONObject().put("json",input).toString(),"UTF-8");return JSONObject(get(api+path+"?input="+q,mapOf("x-trpc-source" to "client"))).getJSONObject("result").getJSONObject("data").getJSONObject("json")}
 override suspend fun resolve(url:String):Result<List<ResolvedServer>> = withContext(Dispatchers.IO){runCatching{val clean=url.substringBefore('#').trimEnd('/');val parts=clean.split('/');val slug=parts.getOrNull(parts.size-2)?:error("معرف Donghua غير صالح");val number=parts.last();val series=call("series.getBySlug",JSONObject().put("slug",slug));val ep=call("episodes.getByNumber",JSONObject().put("seriesId",series.getLong("id")).put("episodeNumber",number));buildList{listOf("videoUrl2" to "MP4","videoUrl" to "MKV","videoUrl3" to "MP4 إضافي","filemoonUrl" to "Filemoon").forEach{(key,label)->val value=ep.optString(key);if(value.isNotBlank()&&value!="available")add(ResolvedServer(value,label,!key.contains("filemoon")))}}}}
}

class WitAnimeServerResolver:ResolverHttp(),ServerResolver{
 override val id="witanime";override fun canHandle(url:String)=url.contains("witanime.site")
 override suspend fun resolve(url:String):Result<List<ResolvedServer>> = withContext(Dispatchers.IO){runCatching{val page=url.substringBefore('#');val html=get(page);val doc=Jsoup.parse(html,page);val direct=doc.select("video source,video[src],a[download]").mapNotNull{it.absUrl("src").ifBlank{it.absUrl("href")}.ifBlank{null}};val embeds=doc.select("iframe[src]").mapNotNull{it.absUrl("src").ifBlank{null}};val sourcePath=Regex("sourcesUrl\\s*:\\s*['\\\"]([^'\\\"]+)").find(html)?.groupValues?.get(1);val api=sourcePath?.let{if(it.startsWith("http"))it else "https://witanime.site"+it};val apiLinks=api?.let{runCatching{val body=get(it,mapOf("Accept" to "application/json","Referer" to page));Regex("https?://[^\\\" ]+").findAll(body).map{m->m.value}.toList()}.getOrDefault(emptyList())}.orEmpty();val links=(direct+apiLinks+embeds).distinct().map{ResolvedServer(it,if(it.contains("iframe")||it.contains("embed"))"تضمين" else "سيرفر WitAnime",!it.contains("embed")&&!it.contains("iframe"))};if(links.isEmpty())listOf(ResolvedServer(page,"صفحة المشاهدة",false))else links}}
}

class AnimeLekServerResolver:ResolverHttp(),ServerResolver{
 override val id="animelek";override fun canHandle(url:String)=url.contains("animelek.cc")||url.contains("animedar.net")
 override suspend fun resolve(url:String):Result<List<ResolvedServer>> = withContext(Dispatchers.IO){runCatching{val page=url.substringBefore('#');val html=get(page);val doc=Jsoup.parse(html,page);val links=(doc.select("video source,video[src],iframe[src],[data-video],[data-src]").mapNotNull{it.absUrl("src").ifBlank{it.absUrl("data-video")}.ifBlank{it.absUrl("data-src")}.ifBlank{null}}+Regex("https?://[^\\\"' ]+").findAll(html).map{it.value}.filter{it.contains(".m3u8")||it.contains(".mp4")}.toList()).distinct().map{ResolvedServer(it,"Animelek",it.contains(".m3u8")||it.contains(".mp4"))};if(links.isEmpty())listOf(ResolvedServer(page,"صفحة الحلقة",false))else links}}
}

class ServerResolverRegistry(private val resolvers:List<ServerResolver>){fun forUrl(url:String)=resolvers.firstOrNull{it.canHandle(url)};suspend fun resolve(url:String)=forUrl(url)?.resolve(url)?:Result.success(listOf(ResolvedServer(url,"صفحة المصدر",false)))}
