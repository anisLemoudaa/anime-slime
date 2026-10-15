package com.animeslime.app
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
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

@Composable fun WebEpisodeScreen(url:String,onBack:()->Unit){var loading by remember{mutableStateOf(true)};val context=androidx.compose.ui.platform.LocalContext.current;val webView=remember{WebView(context)};BackHandler{if(webView.canGoBack())webView.goBack()else onBack()};Column(Modifier.fillMaxSize().background(Ink)){Row(Modifier.fillMaxWidth().padding(8.dp),verticalAlignment=Alignment.CenterVertically){TextButton(onClick=onBack){Text("رجوع")};Text("مشغل المصدر",color=Color.White)};Box(Modifier.weight(1f)){AndroidView(factory={webView.apply{layoutParams=ViewGroup.LayoutParams(-1,-1);settings.javaScriptEnabled=true;settings.domStorageEnabled=true;settings.mediaPlaybackRequiresUserGesture=false;settings.allowFileAccess=true;webViewClient=object:WebViewClient(){override fun onPageFinished(view:WebView?,url:String?){loading=false}};webChromeClient=WebChromeClient();loadUrl(url,mapOf("User-Agent" to "Mozilla/5.0 (Android) AnimeSlime/1.0"))}},modifier=Modifier.fillMaxSize());if(loading)CircularProgressIndicator(Modifier.align(Alignment.Center),color=Color.White)}}}
