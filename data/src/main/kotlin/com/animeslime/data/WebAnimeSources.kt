package com.animeslime.data

import com.animeslime.sourceapi.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.jsoup.Jsoup
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

abstract class WebSource : AnimeCatalogueSource {
    protected val http = OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build()
    protected fun html(url: String): org.jsoup.nodes.Document = http.newCall(Request.Builder().url(url).header("User-Agent", "Mozilla/5.0 (Android) Anime Slime/2.0").build()).execute().use { response ->
        if (!response.isSuccessful) throw SourceException("HTTP ${response.code} من $url")
        Jsoup.parse(response.body?.string().orEmpty(), url)
    }
    protected fun clean(value: String?) = value.orEmpty().replace(Regex("\\s+"), " ").trim()
    override suspend fun popular(page: Int) = latest(page)
    override suspend fun hosts(episode: SEpisode) = episode.hosters
    override suspend fun videos(hoster: Hoster) = listOf(Video(hoster.url, hoster.name, hoster.url))
}

class AnimeLekAnimeSource : WebSource() {
    override val id = 1L
    override val name = "Animelek"
    override val lang = "ar"
    private val base = "https://animelek.cc"
    override suspend fun latest(page: Int) = withContext(Dispatchers.IO) { parseCards(html("$base/home")) }
    override suspend fun search(query: String, page: Int, filters: List<AnimeFilter>) = withContext(Dispatchers.IO) { parseCards(html("$base/?s=${URLEncoder.encode(query, "UTF-8")}")) }
    private fun parseCards(doc: org.jsoup.nodes.Document): AnimePage {
        val items = doc.select("a[href*='/anime/'], .anime-card a, .bsx a").mapNotNull { anchor ->
            val url = anchor.absUrl("href"); val title = clean(anchor.selectFirst("h2,h3,.title")?.text().ifNullOr(anchor.text()))
            if (url.isBlank() || title.length < 2) null else SAnime(url, title, anchor.selectFirst("img")?.absUrl("src"))
        }.distinctBy { it.url }.take(60)
        return AnimePage(items, items.size >= 60)
    }
    override suspend fun details(anime: SAnime) = withContext(Dispatchers.IO) {
        val doc = html(anime.url); anime.copy(title = clean(doc.selectFirst("h1,h2,title")?.text().ifNullOr(anime.title)), thumbnailUrl = doc.selectFirst("meta[property=og:image]")?.attr("content").ifNullOr(anime.thumbnailUrl), description = clean(doc.selectFirst(".description,.story,.summary")?.text()))
    }
    override suspend fun episodes(anime: SAnime) = withContext(Dispatchers.IO) { html(anime.url).select("a[href*='/episode/'], a[href*='/watch/']").mapNotNull { a -> val url = a.absUrl("href"); val number = Regex("(\\d+(?:\\.\\d+)?)").find(a.text()+" "+url)?.groupValues?.get(1)?.toFloatOrNull() ?: return@mapNotNull null; SEpisode(url, clean(a.text()).ifBlank { "الحلقة $number" }, number, hosters = listOf(Hoster("صفحة المشاهدة", url))) }.distinctBy { it.number }.sortedBy { it.number } }
}

class WitAnimeAnimeSource : WebSource() {
    override val id = 2L
    override val name = "WitAnime"
    override val lang = "ar"
    private val base = "https://witanime.site"
    override suspend fun latest(page: Int) = withContext(Dispatchers.IO) { parseCards(html(base)) }
    override suspend fun search(query: String, page: Int, filters: List<AnimeFilter>) = withContext(Dispatchers.IO) { parseCards(html("$base/?s=${URLEncoder.encode(query, "UTF-8")}")) }
    private fun parseCards(doc: org.jsoup.nodes.Document) = doc.select("a[href*='/anime/']").mapNotNull { a -> val url=a.absUrl("href");val title=clean(a.selectFirst("h2,h3")?.text().ifNullOr(a.text()));if(url.isBlank()||title.length<2)null else SAnime(url,title,a.selectFirst("img")?.absUrl("src")) }.distinctBy{it.url}.take(60).let{AnimePage(it,it.size>=60)}
    override suspend fun details(anime: SAnime) = withContext(Dispatchers.IO) { val doc=html(anime.url);anime.copy(title=clean(doc.selectFirst("h1,h2,title")?.text().ifNullOr(anime.title)),thumbnailUrl=doc.selectFirst("meta[property=og:image]")?.attr("content").ifNullOr(anime.thumbnailUrl)) }
    override suspend fun episodes(anime: SAnime) = withContext(Dispatchers.IO) { html(anime.url).select("a[href*='/watch/']").mapNotNull{a->val url=a.absUrl("href");val n=Regex("/(\\d+)$").find(url)?.groupValues?.get(1)?.toFloatOrNull()?:return@mapNotNull null;SEpisode(url,clean(a.text()).ifBlank{"الحلقة $n"},n,hosters=listOf(Hoster("WitAnime",url)))}.distinctBy{it.number}.sortedBy{it.number} }
}

class DonghuaArAnimeSource : WebSource() {
    override val id = 3L
    override val name = "Donghua-Ar"
    override val lang = "ar"
    private val base = "https://donghua-ar.me"
    private val api = "$base/api/trpc/"
    private fun callArray(path: String, input: JSONObject): List<JSONObject> { val q=URLEncoder.encode(JSONObject().put("json",input).toString(),"UTF-8");return http.newCall(Request.Builder().url("$api$path?input=$q").header("x-trpc-source","client").build()).execute().use{r->if(!r.isSuccessful)throw SourceException("HTTP ${r.code}");val a=JSONObject(r.body?.string().orEmpty()).getJSONObject("result").getJSONObject("data").getJSONArray("json");(0 until a.length()).map{a.getJSONObject(it)}} }
    override suspend fun latest(page: Int)=withContext(Dispatchers.IO){val list=callArray("series.latest",JSONObject().put("limit",50));AnimePage(list.map{SAnime("$base/series/${it.optString("slug")}",it.optString("title").ifBlank{it.optString("titleEn")},it.optString("image").ifBlank{null},description=it.optString("description").ifBlank{null})},false)}
    override suspend fun search(query:String,page:Int,filters:List<AnimeFilter>)=latest(page).let{AnimePage(it.items.filter{a->a.title.contains(query,true)},false)}
    override suspend fun details(anime:SAnime)=anime
    override suspend fun episodes(anime:SAnime): List<SEpisode> = emptyList()
}

private fun String?.ifNullOr(fallback: String?) = if (this.isNullOrBlank()) fallback.orEmpty() else this
