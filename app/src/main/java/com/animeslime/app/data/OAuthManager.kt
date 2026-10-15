package com.animeslime.app.data
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.animeslime.app.BuildConfig
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.*
import java.security.SecureRandom
import java.util.Base64
import java.util.concurrent.TimeUnit

data class OAuthResult(val provider:String,val code:String?,val state:String?,val error:String?)
object OAuthBus{private val _events=MutableSharedFlow<OAuthResult>(extraBufferCapacity=4);val events=_events.asSharedFlow();fun emit(uri:Uri){_events.tryEmit(OAuthResult(if(uri.getQueryParameter("provider")=="mal")"mal" else "anilist",uri.getQueryParameter("code"),uri.getQueryParameter("state"),uri.getQueryParameter("error")))}}
class OAuthManager(private val context:Context){
 private val http=OkHttpClient.Builder().connectTimeout(15,TimeUnit.SECONDS).readTimeout(20,TimeUnit.SECONDS).build()
 private val verifier=mutableMapOf<String,String>()
 private fun state(provider:String)=Base64.getUrlEncoder().withoutPadding().encodeToString(ByteArray(18).also{SecureRandom().nextBytes(it)})+provider
 fun start(provider:String){val s=state(provider);val v=Base64.getUrlEncoder().withoutPadding().encodeToString(ByteArray(32).also{SecureRandom().nextBytes(it)});verifier[s]=v;val uri=if(provider=="mal")Uri.parse("https://myanimelist.net/v1/oauth2/authorize").buildUpon().appendQueryParameter("response_type","code").appendQueryParameter("client_id",BuildConfig.MAL_CLIENT_ID).appendQueryParameter("state",s).appendQueryParameter("redirect_uri",BuildConfig.OAUTH_REDIRECT_URI).appendQueryParameter("code_challenge",v).appendQueryParameter("code_challenge_method","plain").build() else Uri.parse("https://anilist.co/api/v2/oauth/authorize").buildUpon().appendQueryParameter("client_id",BuildConfig.ANILIST_CLIENT_ID).appendQueryParameter("redirect_uri",BuildConfig.OAUTH_REDIRECT_URI).appendQueryParameter("response_type","code").build();context.startActivity(Intent(Intent.ACTION_VIEW,uri))}
 fun exchange(result:OAuthResult,callback:(Result<String>)->Unit){val code=result.code?:return callback(Result.failure(IllegalArgumentException("OAuth code missing")));if(result.provider=="mal"){val v=verifier.remove(result.state).orEmpty();val form=FormBody.Builder().add("client_id",BuildConfig.MAL_CLIENT_ID).add("grant_type","authorization_code").add("code",code).add("redirect_uri",BuildConfig.OAUTH_REDIRECT_URI).add("code_verifier",v).build();request("https://myanimelist.net/v1/oauth2/token",form,callback)}else{val form=FormBody.Builder().add("client_id",BuildConfig.ANILIST_CLIENT_ID).add("client_secret",BuildConfig.ANILIST_CLIENT_SECRET).add("redirect_uri",BuildConfig.OAUTH_REDIRECT_URI).add("code",code).add("grant_type","authorization_code").build();request("https://anilist.co/api/v2/oauth/access_token",form,callback)}}
 private fun request(url:String,body:RequestBody,callback:(Result<String>)->Unit){Thread{runCatching{http.newCall(Request.Builder().url(url).post(body).build()).execute().use{r->val text=r.body?.string().orEmpty();require(r.isSuccessful){"OAuth HTTP ${r.code}: $text"};text}}.let(callback)}.start()}
}
