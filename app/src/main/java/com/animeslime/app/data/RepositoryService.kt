package com.animeslime.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class RepositoryService(private val parser:RepositoryIndexParser=RepositoryIndexParser()){
 private val client=OkHttpClient.Builder().connectTimeout(15,TimeUnit.SECONDS).readTimeout(25,TimeUnit.SECONDS).build()
 suspend fun fetch(url:String):Result<RepositoryIndex>=withContext(Dispatchers.IO){runCatching{require(url.startsWith("https://")||url.startsWith("http://")){"رابط غير صالح"};val req=Request.Builder().url(url).header("User-Agent","AnimeSlime/0.2").build();client.newCall(req).execute().use{res->require(res.isSuccessful){"تعذر الاتصال: HTTP ${res.code}"};parser.parse(res.body?.string().orEmpty()).getOrThrow()}}}
}
