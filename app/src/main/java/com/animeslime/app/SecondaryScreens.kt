package com.animeslime.app

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.animeslime.app.data.Repository
import com.animeslime.app.data.Extension
import com.animeslime.app.ui.RepositoryViewModel
import com.animeslime.app.ui.SettingsViewModel
import com.animeslime.app.ui.LibraryViewModel
import com.animeslime.app.ui.theme.*

@Composable fun LibraryScreen(open:()->Unit){val vm:LibraryViewModel=hiltViewModel();val items by vm.items.collectAsState();LazyColumn(Modifier.fillMaxSize().background(Ink)){
 item{Header("المكتبة")}; item{Row(Modifier.horizontalScroll(rememberScrollState()).padding(horizontal=20.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)){Chip("الكل 24",true);Chip("أشاهده 9");Chip("مكتمل 8");Chip("أخطط 7")}}
 item{Surface(color=Surface,shape=RoundedCornerShape(18.dp),modifier=Modifier.padding(20.dp,14.dp)){Row(Modifier.padding(14.dp),verticalAlignment=Alignment.CenterVertically){Text("3 حلقات جديدة · آخر تحديث قبل 3 ساعات",Modifier.weight(1f),fontSize=13.sp);IconButton({}){Icon(Icons.Default.Refresh,"تحديث",tint=Mint)}}}}
 if(items.isEmpty()) item{Text("مكتبتك فارغة. افتح عملًا من التصفح واضغط + المكتبة.",color=Muted,modifier=Modifier.padding(20.dp))} else items(items){anime->Poster(anime.title,open)}
 }}
@Composable fun Poster(title:String,open:()->Unit){Row(Modifier.fillMaxWidth().padding(horizontal=20.dp,vertical=5.dp).clickable{open()},verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(72.dp,96.dp).clip(RoundedCornerShape(14.dp)).background(Brush.linearGradient(listOf(Mint,Color(0xFF1E1B4B)))));Column(Modifier.padding(12.dp)){Text(title,fontWeight=FontWeight.Bold);Text("12 حلقة · 3 غير مشاهدة",color=Muted,fontSize=12.sp)}}}

@Composable fun UpdatesScreen()=LazyColumn(Modifier.fillMaxSize().background(Ink)){item{Header("التحديثات")};item{Row(Modifier.padding(horizontal=20.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)){Chip("التحديثات",true);Chip("السجل");Chip("التنزيلات")}};items(listOf("ظل الفجر · الحلقة 4","كوكب الحدود · الحلقة 11","ملاذ النجوم · الحلقة 12")){NewRow(it,"متاح الآن")}}

@Composable fun BrowseScreen(){
 val vm:RepositoryViewModel=hiltViewModel()
 val state by vm.state.collectAsState()
 var tab by remember{mutableStateOf(0)}
 var url by remember{mutableStateOf("")}
 LazyColumn(Modifier.fillMaxSize().background(Ink)) {
  item { Header("التصفح") }
  item { Row(Modifier.padding(horizontal=20.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)){Chip("المصادر",tab==0){tab=0};Chip("الإضافات",tab==1){tab=1};Chip("المستودعات",tab==2){tab=2}} }
  if(tab==2){
   item { Text("أضف مستودعًا",color=Mint,fontWeight=FontWeight.Bold,modifier=Modifier.padding(20.dp,18.dp,20.dp,8.dp)) }
   item { Row(Modifier.padding(horizontal=20.dp),verticalAlignment=Alignment.CenterVertically){OutlinedTextField(value=url,onValueChange={url=it},modifier=Modifier.weight(1f),singleLine=true,label={Text("رابط index.min.json")});Spacer(Modifier.width(8.dp));Button(onClick={vm.add(url)},enabled=url.isNotBlank()&&!state.loading){Text(if(state.loading)"..." else "إضافة")}} }
   state.error?.let{item{Text(it,color=Color(0xFFFF8A80),modifier=Modifier.padding(20.dp))}}
   state.message?.let{item{Text(it,color=Mint,modifier=Modifier.padding(20.dp))}}
   items(state.repositories){repo->RepositoryRow(repo,{vm.refresh(repo)},{vm.toggle(repo)},{vm.remove(repo)})}
  } else if(tab==1){
   item{Text("إضافات المستودعات",color=Mint,fontWeight=FontWeight.Bold,modifier=Modifier.padding(20.dp))}
   if(state.extensions.isEmpty()) item{Text("أضف مستودعًا لفهرسة الإضافات.",color=Muted,modifier=Modifier.padding(20.dp))}
   else items(state.extensions){ext->ExtensionRow(ext,{vm.install(ext)},{vm.uninstall(ext)},{vm.toggleExtension(ext)})}
  } else {
   item{Section("المصادر المتاحة")};item{SourceRow("Animelek · مدمج")};item{SourceRow("WitAnime · مدمج")};item{SourceRow("Donghua-Ar · مدمج")}
   items(state.extensions.filter{it.installed&&it.enabled}){ext->SourceRow(ext.name)}
   if(state.extensions.none{it.installed&&it.enabled}) item{Text("ثبّت إضافة من تبويب الإضافات لإظهار مصدرها هنا.",color=Muted,modifier=Modifier.padding(20.dp))}
  }
 }
}
@Composable fun ExtensionRow(ext:Extension,install:()->Unit,uninstall:()->Unit,toggle:()->Unit){Surface(color=Surface,shape=RoundedCornerShape(18.dp),border=BorderStroke(1.dp,Border),modifier=Modifier.padding(horizontal=20.dp,vertical=5.dp)){Column(Modifier.padding(14.dp)){Row(verticalAlignment=Alignment.CenterVertically){Text(ext.name,Modifier.weight(1f),fontWeight=FontWeight.Bold);Text(if(ext.installed)"مثبّت" else "غير مثبت",color=if(ext.installed)Mint else Muted,fontSize=12.sp)};Text("${ext.lang} · v${ext.version} · ${if(ext.adapter.isBlank())"محول غير محدد" else ext.adapter}",color=Muted,fontSize=12.sp);Row(horizontalArrangement=Arrangement.End,modifier=Modifier.fillMaxWidth()){if(ext.installed){TextButton(toggle){Text(if(ext.enabled)"تعطيل" else "تفعيل")};TextButton(uninstall){Text("إزالة")}}else TextButton(install){Text("تثبيت")}}}}}
@Composable fun SourceRow(name:String){Surface(color=Surface,shape=RoundedCornerShape(18.dp),border=BorderStroke(1.dp,Border),modifier=Modifier.padding(horizontal=20.dp,vertical=5.dp)){Row(Modifier.padding(14.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(Mint));Column(Modifier.weight(1f).padding(horizontal=12.dp)){Text(name,fontWeight=FontWeight.Bold);Text("الأحدث · الشائع · بحث",color=Muted,fontSize=12.sp)};Icon(Icons.Default.PushPin,"تثبيت",tint=Mint)}}}

@Composable fun MoreScreen(openSettings:()->Unit,openBackup:()->Unit={},openAccounts:()->Unit={},openDownloads:()->Unit={})=LazyColumn(Modifier.fillMaxSize().background(Ink)){item{Header("المزيد")};item{Surface(color=Surface,shape=RoundedCornerShape(18.dp),modifier=Modifier.padding(20.dp).clickable{openAccounts()}){Row(Modifier.padding(16.dp),verticalAlignment=Alignment.CenterVertically){Icon(Icons.Default.AccountCircle,null,tint=Mint,modifier=Modifier.size(44.dp));Column(Modifier.padding(horizontal=12.dp)){Text("حساب Anime Slime",fontWeight=FontWeight.Bold);Text("دخول للمزامنة السحابية الاختيارية",color=Muted,fontSize=12.sp)}}}};item{MenuRow("التنزيلات",Icons.Default.Folder,openDownloads)};item{MenuRow("الإحصاءات",Icons.Default.BarChart)};item{MenuRow("الحسابات والمزامنة",Icons.Default.AccountCircle,openAccounts)};item{MenuRow("الإعدادات",Icons.Default.Settings,openSettings)};item{MenuRow("النسخ الاحتياطي والاستعادة",Icons.Default.Backup,openBackup)};item{MenuRow("حول التطبيق",Icons.Default.Info)}}
@Composable fun MenuRow(text:String,icon:androidx.compose.ui.graphics.vector.ImageVector,onClick:()->Unit={}){Surface(color=Surface,shape=RoundedCornerShape(18.dp),modifier=Modifier.padding(horizontal=20.dp,vertical=5.dp).clickable{onClick()}){Row(Modifier.padding(16.dp),verticalAlignment=Alignment.CenterVertically){Icon(icon,null,tint=Mint);Text(text,Modifier.padding(horizontal=14.dp).weight(1f),fontWeight=FontWeight.Bold);Icon(Icons.Default.ChevronLeft,null,tint=Muted)}}}

@Composable fun SettingsScreen(back:()->Unit){
 val vm:SettingsViewModel=hiltViewModel();val settings by vm.settings.collectAsState()
 Column(Modifier.fillMaxSize().background(Ink).verticalScroll(rememberScrollState())){
  Row(Modifier.padding(20.dp),verticalAlignment=Alignment.CenterVertically){IconButton(onClick=back){Icon(Icons.Default.ArrowForward,"رجوع")};Text("الإعدادات",fontSize=28.sp,fontWeight=FontWeight.ExtraBold)}
  Text("المظهر",color=Mint,fontWeight=FontWeight.Bold,modifier=Modifier.padding(20.dp,12.dp));SettingRow("المظهر",if(settings.theme=="dark")"داكن" else settings.theme,false);SettingRow("اللغة",if(settings.language=="ar")"العربية" else "English",false);SettingRow("لون التمييز",settings.accent,false)
  Text("المشغّل",color=Mint,fontWeight=FontWeight.Bold,modifier=Modifier.padding(20.dp,22.dp,20.dp,12.dp));SettingToggle("تخطي المقدمة تلقائيًا",settings.autoIntro){vm.autoIntro(it)};SettingToggle("تشغيل الحلقة التالية",settings.autoNext){vm.autoNext(it)};SettingRow("الجودة الافتراضية","تلقائي",false);SettingRow("مدة القفز","${settings.skipSeconds} ثوان",false)
  Text("التنزيلات والتحديثات",color=Mint,fontWeight=FontWeight.Bold,modifier=Modifier.padding(20.dp,22.dp,20.dp,12.dp));SettingToggle("التنزيل عبر Wi-Fi فقط",settings.wifiOnly){vm.wifiOnly(it)};SettingToggle("إشعارات الحلقات الجديدة",settings.notifications){vm.notifications(it)}
  Text("الخصوصية",color=Mint,fontWeight=FontWeight.Bold,modifier=Modifier.padding(20.dp,22.dp,20.dp,12.dp));SettingRow("الوضع الخفي","لا يحفظ السجل والتقدم",false);SettingRow("قفل التطبيق","معطّل",true)
 }
}
@Composable fun SettingToggle(title:String,checked:Boolean,onChange:(Boolean)->Unit){Surface(color=Surface,shape=RoundedCornerShape(18.dp),modifier=Modifier.padding(horizontal=20.dp,vertical=4.dp)){Row(Modifier.padding(15.dp),verticalAlignment=Alignment.CenterVertically){Text(title,Modifier.weight(1f),fontWeight=FontWeight.Bold);Switch(checked,onChange,colors=SwitchDefaults.colors(checkedThumbColor=Ink,checkedTrackColor=Mint))}}}
@Composable fun SettingRow(title:String,value:String,toggle:Boolean){Surface(color=Surface,shape=RoundedCornerShape(18.dp),modifier=Modifier.padding(horizontal=20.dp,vertical=4.dp)){Row(Modifier.padding(15.dp),verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text(title,fontWeight=FontWeight.Bold);Text(value,color=Muted,fontSize=12.sp)};if(toggle)Switch(checked=value=="مفعّل",onCheckedChange={},colors=SwitchDefaults.colors(checkedThumbColor=Ink,checkedTrackColor=Mint))else Icon(Icons.Default.ChevronLeft,null,tint=Muted)}}}

@Composable
fun DetailsScreen(back:()->Unit, play:()->Unit={}) {
 Column(Modifier.fillMaxSize().background(Ink).verticalScroll(rememberScrollState())) {
  Row(Modifier.padding(14.dp).fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween) {
   IconButton(onClick=back) { Icon(Icons.Default.ArrowForward,"رجوع") }
   IconButton(onClick={}) { Icon(Icons.Default.Share,"مشاركة") }
  }
  Box(Modifier.padding(horizontal=20.dp).fillMaxWidth().height(220.dp).clip(RoundedCornerShape(24.dp)).background(Brush.linearGradient(listOf(Color(0xFF7C3AED),Color(0xFF1E1B4B)))))
  Column(Modifier.padding(20.dp)) {
   Text("ظل الفجر",fontSize=30.sp,fontWeight=FontWeight.ExtraBold)
   Text("أكشن · فانتازيا · 12 حلقة",color=Muted)
   Spacer(Modifier.height(16.dp))
   Button(onClick=play,colors=ButtonDefaults.buttonColors(containerColor=Mint,contentColor=Ink),modifier=Modifier.fillMaxWidth()) { Icon(Icons.Default.PlayArrow,null);Text("ابدأ المشاهدة · الحلقة 1",fontWeight=FontWeight.Bold) }
   Spacer(Modifier.height(12.dp));Text("الحلقات",fontSize=20.sp,fontWeight=FontWeight.Bold)
   listOf(1,2,3,4,5).forEach{EpisodeRow(it)}
  }
 }
}
@Composable fun EpisodeRow(n:Int){Surface(color=Surface,shape=RoundedCornerShape(14.dp),modifier=Modifier.padding(vertical=4.dp)){Row(Modifier.padding(14.dp).fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Text("الحلقة $n",Modifier.weight(1f),fontWeight=FontWeight.Bold);Icon(Icons.Default.Download,"تنزيل",tint=Mint);Spacer(Modifier.width(14.dp));Icon(Icons.Default.PlayCircle,"تشغيل",tint=Mint)}}}
@Composable fun RepositoryRow(repo:Repository,onRefresh:()->Unit,onToggle:()->Unit,onDelete:()->Unit){Surface(color=Surface,shape=RoundedCornerShape(18.dp),border=BorderStroke(1.dp,Border),modifier=Modifier.padding(horizontal=20.dp,vertical=5.dp)){Column(Modifier.padding(14.dp)){Row(verticalAlignment=Alignment.CenterVertically){Text(repo.name,Modifier.weight(1f),fontWeight=FontWeight.Bold);Text(if(repo.enabled)"مفعّل" else "متوقف",color=if(repo.enabled)Mint else Muted,fontSize=12.sp)};Text(repo.url,color=Muted,fontSize=11.sp);Text("${repo.extensionCount} إضافة · ${if(repo.trusted)"موثوق" else "مصدر خارجي"}",color=Muted,fontSize=12.sp);Row(horizontalArrangement=Arrangement.End,modifier=Modifier.fillMaxWidth()){TextButton(onRefresh){Text("تحديث")};TextButton(onToggle){Text(if(repo.enabled)"تعطيل" else "تفعيل")};TextButton(onDelete){Text("حذف",color=Color(0xFFFF8A80))}}}}}
