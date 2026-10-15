package com.animeslime.app
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.animeslime.app.data.SourceEpisode
import com.animeslime.app.ui.SourceDetailsViewModel
import com.animeslime.app.ui.theme.*

@Composable
fun SourceDetailsScreen(url:String,back:()->Unit,play:(String)->Unit){
 val vm:SourceDetailsViewModel=hiltViewModel();val context=LocalContext.current;val state by vm.state.collectAsState()
 LaunchedEffect(url){vm.load(url)}
 Column(Modifier.fillMaxSize().background(Ink)){IconButton(onClick=back){Icon(Icons.Default.ArrowForward,"رجوع")};if(state.loading)LinearProgressIndicator(Modifier.fillMaxWidth());state.error?.let{Text(it,color=Color(0xFFFF8A80),modifier=Modifier.padding(20.dp))};state.details?.let{d->
  Row(Modifier.padding(20.dp),verticalAlignment=Alignment.CenterVertically){Text(d.anime.title,fontSize=28.sp,color=Color.White,modifier=Modifier.weight(1f));Button(onClick=vm::addToLibrary){Text("+ المكتبة")}}
  LazyColumn{items(d.episodes){ep->EpisodeSourceRow(ep,{play(ep.videoLinks.firstOrNull()?:ep.url)},{vm.download(ep,context)})}}
 }}
}
@Composable private fun EpisodeSourceRow(ep:SourceEpisode,play:()->Unit,download:()->Unit){ListItem(headlineContent={Text(ep.title)},supportingContent={Text(if(ep.videoLinks.isEmpty())"فتح صفحة التشغيل" else "${ep.videoLinks.size} روابط فيديو")},trailingContent={Row{Button(onClick=play){Text("تشغيل")};if(ep.videoLinks.any{it.contains(".mp4",true)||it.contains(".mkv",true)})TextButton(onClick=download){Text("تنزيل")}}})}
