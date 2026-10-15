package com.animeslime.app
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.animeslime.app.ui.SourceDetailsViewModel
import com.animeslime.app.ui.theme.*
@Composable fun SourceDetailsScreen(url:String,back:()->Unit,play:(String)->Unit){val vm:SourceDetailsViewModel=hiltViewModel();val state by vm.state.collectAsState();LaunchedEffect(url){vm.load(url)};Column(Modifier.fillMaxSize().background(Ink)){IconButton(back){Icon(Icons.Default.ArrowForward,"رجوع")};if(state.loading)LinearProgressIndicator(Modifier.fillMaxWidth());state.error?.let{Text(it,color=Color(0xFFFF8A80),modifier=Modifier.padding(20.dp))};state.details?.let{d->Row(Modifier.padding(20.dp),verticalAlignment=Alignment.CenterVertically){Text(d.anime.title,fontSize=28.sp,color=Color.White,modifier=Modifier.weight(1f));Button(vm::addToLibrary){Text("+ المكتبة")}};LazyColumn{items(d.episodes){ep->ListItem(headlineContent={Text(ep.title)},supportingContent={Text(if(ep.videoLinks.isEmpty())"فتح صفحة التشغيل" else "${ep.videoLinks.size} روابط فيديو")},trailingContent={Button({play(ep.videoLinks.firstOrNull()?:ep.url)}){Text("تشغيل")}})}}}}}
