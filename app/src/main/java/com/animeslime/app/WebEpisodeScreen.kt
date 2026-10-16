package com.animeslime.app
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.animeslime.app.ui.theme.Ink

@Composable fun WebEpisodeScreen(url:String,onBack:()->Unit){
 var loading by remember{mutableStateOf(true)};var error by remember{mutableStateOf<String?>(null)}
 val context=androidx.compose.ui.platform.LocalContext.current;val webView=remember{WebView(context)}
 BackHandler{if(webView.canGoBack())webView.goBack()else onBack()}
 Column(Modifier.fillMaxSize().background(Ink)){Row(Modifier.fillMaxWidth().padding(8.dp),verticalAlignment=Alignment.CenterVertically){TextButton(onClick=onBack){Text("رجوع")};Text("مشغل المصدر",color=Color.White)};Box(Modifier.weight(1f)){
  if(url.startsWith("http://",true)||url.startsWith("https://",true)) AndroidView(factory={webView.apply{layoutParams=ViewGroup.LayoutParams(-1,-1);settings.javaScriptEnabled=true;settings.domStorageEnabled=true;settings.mediaPlaybackRequiresUserGesture=false;settings.allowFileAccess=false;settings.allowContentAccess=false;webViewClient=object:WebViewClient(){override fun onPageFinished(view:WebView?,loadedUrl:String?){loading=false};override fun onReceivedError(view:WebView?,request:WebResourceRequest?,receivedError:WebResourceError?){if(request?.isForMainFrame!=false){loading=false;error=receivedError?.description?.toString()?:"تعذر تحميل صفحة المصدر"}}};webChromeClient=WebChromeClient();loadUrl(url,mapOf("User-Agent" to "Mozilla/5.0 (Android) AnimeSlime/1.0"))}},modifier=Modifier.fillMaxSize())
  else {loading=false;error="رابط الحلقة غير صالح أو محلي: $url"}
  if(loading)CircularProgressIndicator(Modifier.align(Alignment.Center),color=Color.White)
  error?.let{Column(Modifier.align(Alignment.Center).padding(24.dp),horizontalAlignment=Alignment.CenterHorizontally){Text("تعذر فتح الحلقة",color=Color.White,style=MaterialTheme.typography.titleLarge);Text(it,color=Color.LightGray);TextButton(onClick=onBack){Text("رجوع")}}}
 }}
}
