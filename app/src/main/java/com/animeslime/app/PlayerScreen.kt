package com.animeslime.app

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerView

@Composable fun PlayerScreen(videoUrl:String?,onBack:()->Unit){
 if(videoUrl.isNullOrBlank()){Box(Modifier.fillMaxSize().background(Color.Black),contentAlignment=Alignment.Center){Column(horizontalAlignment=Alignment.CenterHorizontally){Text("لا يوجد رابط فيديو من المصدر بعد",color=Color.White);TextButton(onBack){Text("رجوع")}}};return}
 val context=LocalContext.current;var locked by remember{mutableStateOf(false)};var speed by remember{mutableFloatStateOf(1f)}
 val player=remember(videoUrl){ExoPlayer.Builder(context).build().apply{setMediaItem(MediaItem.fromUri(videoUrl));prepare();playWhenReady=true}}
 val session=remember(player){MediaSession.Builder(context,player).setCallback(object:MediaSession.Callback{}).build()}
 DisposableEffect(player){onDispose{session.release();player.release()}}
 Box(Modifier.fillMaxSize().background(Color.Black).pointerInput(locked){detectTapGestures(onDoubleTap={offset->if(!locked){if(offset.x<300)player.seekBack() else player.seekForward()}})}){
  AndroidView(factory={PlayerView(it).apply{this.player=player;layoutParams=ViewGroup.LayoutParams(-1,-1)}},modifier=Modifier.fillMaxSize())
  if(!locked)Row(Modifier.align(Alignment.BottomCenter).padding(16.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)){Button({player.seekBack()}){Text("⟲ 10")};Button({speed=if(speed>=2f)0.5f else speed+0.5f;player.setPlaybackParameters(PlaybackParameters(speed))}){Text("${speed}x")};Button({player.seekForward()}){Text("10 ⟳")};Button({locked=true}){Text("قفل")}} else Button({locked=false},modifier=Modifier.align(Alignment.Center)){Text("فتح اللمس")}
 }
}
