package com.animeslime.app
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.animeslime.app.ui.DownloadViewModel
import com.animeslime.app.data.DownloadRow
import com.animeslime.app.ui.theme.*

@Composable fun DownloadsScreen(back:()->Unit){val vm:DownloadViewModel=hiltViewModel();val items by vm.items.collectAsState();Column(Modifier.fillMaxSize().background(Ink)){Row(Modifier.fillMaxWidth().padding(14.dp),horizontalArrangement=Arrangement.SpaceBetween){Text("التنزيلات",style=MaterialTheme.typography.headlineMedium);TextButton(back){Text("رجوع")}};if(items.isEmpty())Text("لا توجد تنزيلات بعد. اختر رابط MP4 مباشرًا من صفحة الحلقة.",color=Muted,modifier=Modifier.padding(20.dp))else LazyColumn{items(items,key={it.episodeId}){item->DownloadRowView(item,{vm.cancel(item)},{vm.retry(item)},{vm.remove(item)})}}}}
@Composable private fun DownloadRowView(item:DownloadRow,cancel:()->Unit,retry:()->Unit,remove:()->Unit){Surface(color=Surface,modifier=Modifier.fillMaxWidth().padding(horizontal=16.dp,vertical=5.dp)){Column(Modifier.padding(14.dp)){Text("${item.animeTitle} · الحلقة ${item.number}",color=Color.White);Text(item.stateLabel(),color=if(item.state=="COMPLETED")Mint else Muted,fontSize=12.sp);LinearProgressIndicator(progress={item.progress/100f},modifier=Modifier.fillMaxWidth().padding(vertical=8.dp));Row(horizontalArrangement=Arrangement.End,modifier=Modifier.fillMaxWidth()){if(item.state=="RUNNING"||item.state=="QUEUED")TextButton(cancel){Text("إيقاف")};if(item.state=="FAILED"||item.state=="CANCELLED"||item.state=="PAUSED")TextButton(retry){Text("استئناف")};TextButton(remove){Text("حذف")}}}}}
private fun DownloadRow.stateLabel()=when(state){"RUNNING"->"جارٍ التنزيل · $progress%";"COMPLETED"->"اكتمل · ${filePath.orEmpty()}";"FAILED"->"فشل: ${error.orEmpty()}";"CANCELLED"->"أُلغي";"PAUSED"->"متوقف مؤقتًا";else->"في الانتظار"}
