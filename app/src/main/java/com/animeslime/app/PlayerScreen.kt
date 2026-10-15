package com.animeslime.app

import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable fun PlayerScreen(videoUrl:String?,onBack:()->Unit){
 if(videoUrl.isNullOrBlank()){androidx.compose.material3.Text("لا يوجد رابط فيديو من المصدر بعد",modifier=Modifier.fillMaxSize());return}
 val context=androidx.compose.ui.platform.LocalContext.current
 val player=remember(videoUrl){ExoPlayer.Builder(context).build().apply{setMediaItem(MediaItem.fromUri(videoUrl));prepare();playWhenReady=true}}
 DisposableEffect(player){onDispose{player.release()}}
 AndroidView(factory={PlayerView(it).apply{this.player=player;layoutParams=ViewGroup.LayoutParams(-1,-1);setShowNextButton(true);setShowPreviousButton(true)}},modifier=Modifier.fillMaxSize())
}
