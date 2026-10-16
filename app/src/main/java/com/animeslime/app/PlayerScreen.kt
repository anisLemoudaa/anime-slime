package com.animeslime.app

import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaSession
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

@androidx.annotation.OptIn(markerClass = [androidx.media3.common.util.UnstableApi::class])
@Composable
fun PlayerScreen(videoUrl:String?,onBack:()->Unit){
 if(videoUrl.isNullOrBlank()){MessagePlayer("لا يوجد رابط فيديو من المصدر بعد",onBack);return}
 val context=LocalContext.current
 var locked by remember(videoUrl){mutableStateOf(false)}
 var speed by remember(videoUrl){mutableFloatStateOf(1f)}
 var loading by remember(videoUrl){mutableStateOf(true)}
 var error by remember(videoUrl){mutableStateOf<String?>(null)}
 val player=remember(videoUrl){
  val http=DefaultHttpDataSource.Factory().setUserAgent("Mozilla/5.0 (Android) AnimeSlime/1.0").setAllowCrossProtocolRedirects(true)
  ExoPlayer.Builder(context).setMediaSourceFactory(DefaultMediaSourceFactory(http)).build().apply{val item=MediaItem.Builder().setUri(videoUrl).apply{if(videoUrl.contains(".m3u8",true))setMimeType(MimeTypes.APPLICATION_M3U8)}.build();setMediaItem(item);prepare();playWhenReady=true}
 }
 val session=remember(player){MediaSession.Builder(context,player).build()}
 DisposableEffect(player){val listener=object:Player.Listener{override fun onPlaybackStateChanged(state:Int){loading=state==Player.STATE_BUFFERING||state==Player.STATE_IDLE};override fun onPlayerError(exception:PlaybackException){loading=false;error=exception.errorCodeName+": "+(exception.message ?: "خطأ غير معروف")} };player.addListener(listener);onDispose{player.removeListener(listener);session.release();player.release()}}
 BackHandler(onBack=onBack)
 Box(Modifier.fillMaxSize().background(Color.Black).pointerInput(locked){detectTapGestures(onDoubleTap={offset->if(!locked){if(offset.x<300)player.seekBack() else player.seekForward()}})}){
  AndroidView(factory={ctx->PlayerView(ctx).apply{this.player=player;useController=true;controllerShowTimeoutMs=3500;resizeMode=AspectRatioFrameLayout.RESIZE_MODE_FIT;layoutParams=ViewGroup.LayoutParams(-1,-1)}},update={it.player=player},modifier=Modifier.fillMaxSize())
  if(loading&&error==null)CircularProgressIndicator(color=Color.White,modifier=Modifier.align(Alignment.Center))
  error?.let{Column(Modifier.align(Alignment.Center).padding(24.dp),horizontalAlignment=Alignment.CenterHorizontally){Text("تعذر تشغيل الفيديو",color=Color.White,style=MaterialTheme.typography.titleLarge);Text(it,color=Color.LightGray);Spacer(Modifier.height(12.dp));Row{Button(onClick={error=null;loading=true;player.seekTo(0);player.prepare();player.playWhenReady=true}){Text("إعادة المحاولة")};TextButton(onClick=onBack){Text("رجوع")}}}}
  if(!locked&&error==null)Row(Modifier.align(Alignment.BottomCenter).padding(bottom=86.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)){Button(onClick={player.seekBack()}){Text("⟲ 10")};Button(onClick={speed=if(speed>=2f)0.5f else speed+0.5f;player.setPlaybackParameters(PlaybackParameters(speed))}){Text("${speed}x")};Button(onClick={player.seekForward()}){Text("10 ⟳")};Button(onClick={locked=true}){Text("قفل")}}
  if(locked)Button(onClick={locked=false},modifier=Modifier.align(Alignment.TopEnd).padding(16.dp)){Text("فتح التحكم")}
 }
}
@Composable private fun MessagePlayer(message:String,onBack:()->Unit){Box(Modifier.fillMaxSize().background(Color.Black),contentAlignment=Alignment.Center){Column(horizontalAlignment=Alignment.CenterHorizontally){Text(message,color=Color.White);TextButton(onClick=onBack){Text("رجوع")}}}}
