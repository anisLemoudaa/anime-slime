package com.animeslime.app.data

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class TrackerUpdate(val service:String,val remoteId:String,val status:String?,val score:Int?,val progress:Int)
class TrackerService {
 private val http=OkHttpClient.Builder().connectTimeout(15,TimeUnit.SECONDS).readTimeout(20,TimeUnit.SECONDS).build()
 fun aniListUpdate(token:String,mediaId:String,progress:Int,status:String?=null,score:Int?=null):Result<Unit> = runCatching{val fields=JSONObject().put("mediaId",mediaId).put("progress",progress);status?.let{fields.put("status",it)};score?.let{fields.put("score",it)};val query="mutation(\$mediaId:Int,\$progress:Int,\$status:MediaListStatus,\$score:Int){SaveMediaListEntry(mediaId:\$mediaId,progress:\$progress,status:\$status,score:\$score){id}}";post("https://graphql.anilist.co",token,JSONObject().put("query",query).put("variables",fields))}
 fun malUpdate(token:String,remoteId:String,progress:Int,status:String?=null,score:Int?=null):Result<Unit> = runCatching{val form=FormBody.Builder().add("num_watched_episodes",progress.toString()).apply{status?.let{add("status",it)};score?.let{add("score",it.toString())}}.build();val req=Request.Builder().url("https://api.myanimelist.net/v2/anime/$remoteId/my_list_status").put(form).header("Authorization","Bearer $token").build();http.newCall(req).execute().use{require(it.isSuccessful){"MAL HTTP ${it.code}"}}}
 private fun post(url:String,token:String,body:JSONObject){val req=Request.Builder().url(url).post(body.toString().toRequestBody("application/json".toMediaType())).header("Authorization","Bearer $token").build();http.newCall(req).execute().use{require(it.isSuccessful){"Tracker HTTP ${it.code}"}}}
}
data class OAuthConfig(val anilistClientId:String?,val malClientId:String?)
