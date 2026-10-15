package com.animeslime.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

data class SourceAnime(val url:String,val title:String,val cover:String?=null,val description:String?=null)
class AnimeLekSource{
 val baseUrl="https://animelek.cc/home"
 private val client=OkHttpClient.Builder().connectTimeout(15,TimeUnit.SECONDS).readTimeout(25,TimeUnit.SECONDS).build()
 suspend fun search(query:String):Result<List<SourceAnime>> =fetch("${baseUrl}?s=${URLEncoder.encode(query,"UTF-8")}")
 suspend fun latest():Result<List<SourceAnime>> =fetch(baseUrl)
 private suspend fun fetch(url:String):Result<List<SourceAnime>> =withContext(Dispatchers.IO){runCatching{parseCards(get(url))}}
 private fun get(url:String):Document{val req=Request.Builder().url(url).header("User-Agent","Mozilla/5.0 (Android) AnimeSlime/1.0").build();return client.newCall(req).execute().use{r->require(r.isSuccessful){"فشل تحميل Animelek: ${r.code}"};Jsoup.parse(r.body?.string().orEmpty(),url)}}
 private fun parseCards(doc:Document):List<SourceAnime>{val cards=doc.select(".anime-card-container,article");val candidates=if(cards.isEmpty())doc.select("a[href*='/anime/']") else cards;return candidates.mapNotNull{el->val link=if(el.tagName()=="a")el else el.selectFirst("a[href*='/anime/']")?:return@mapNotNull null;val url=link.absUrl("href").ifBlank{link.attr("href")};val title=(el.selectFirst(".anime-card-title a,h2 a,h3 a,img")?.let{it.attr("alt").ifBlank{it.text()}}?:link.attr("aria-label").ifBlank{link.text()}).replace(" - انمي ليك","").trim();if(!url.contains("/anime/")||title.length<2)null else SourceAnime(url,title,el.selectFirst("img")?.absUrl("src")?.ifBlank{el.selectFirst("img")?.absUrl("data-src").orEmpty()}?.ifBlank{null})}.distinctBy{it.url}.take(100)}
 suspend fun details(url:String):Result<SourceDetails> =withContext(Dispatchers.IO){runCatching{val doc=get(url);val anime=SourceAnime(url,doc.selectFirst("h1,.entry-title,.post-title,title")?.text()?.trim().orEmpty().ifBlank{"أنمي"},doc.selectFirst("meta[property=og:image]")?.attr("content"),doc.selectFirst(".description,.story,.entry-content,p")?.text());val linked=doc.select("a[href]").mapNotNull{a->val text=a.text().trim();val n=Regex("(?:الحلقة|episode|ep\\.?)[^0-9]*(\\d+(?:\\.\\d+)?)",RegexOption.IGNORE_CASE).find(text)?.groupValues?.get(1)?.toDoubleOrNull()?:return@mapNotNull null;Triple(a.absUrl("href"),n,text.ifBlank{"الحلقة $n"})};val pages=if(linked.isNotEmpty())linked else doc.select("#EpList1 .CSB").mapNotNull{el->val n=Regex("\\d+(?:\\.\\d+)?").find(el.text())?.value?.toDoubleOrNull()?:return@mapNotNull null;Triple("$url#episode-${el.id().removePrefix("IDSB")}",n,el.text())};val episodes=pages.distinctBy{it.second}.sortedBy{it.second}.map{(episodeUrl,n,title)->SourceEpisode(episodeUrl,n,title,if("#episode-" in episodeUrl)emptyList() else extractVideos(get(episodeUrl)))};SourceDetails(anime,episodes,doc.select(".genres a,.genre a").eachText(),null)}}
 private fun extractVideos(doc:Document):List<String>{val attrs=listOf("src","data-src","data-video","data-url");return doc.select("iframe,video source,video").flatMap{el->attrs.mapNotNull{el.attr(it).takeIf(String::isNotBlank)}}.map{if(it.startsWith("//"))"https:$it" else it}.distinct()}
}
