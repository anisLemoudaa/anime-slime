package com.animeslime.app
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
@Composable fun WebEpisodeScreen(url:String){AndroidView(factory={context->WebView(context).apply{settings.javaScriptEnabled=true;settings.domStorageEnabled=true;settings.mediaPlaybackRequiresUserGesture=false;webViewClient=WebViewClient();loadUrl(url, mapOf("User-Agent" to "Mozilla/5.0 (Android) AnimeSlime/1.0"))}},modifier=Modifier.fillMaxSize())}
