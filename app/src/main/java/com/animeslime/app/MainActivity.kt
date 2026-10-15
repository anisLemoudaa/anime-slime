package com.animeslime.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.*
import com.animeslime.app.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { AnimeSlimeTheme { AnimeSlimeApp() } } }
}

data class NavItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable fun AnimeSlimeApp() {
    val nav = rememberNavController()
    val items = listOf(NavItem("home","الرئيسية",Icons.Default.Home),NavItem("library","المكتبة",Icons.Default.VideoLibrary),NavItem("updates","التحديثات",Icons.Default.Notifications),NavItem("browse","التصفح",Icons.Default.Explore),NavItem("more","المزيد",Icons.Default.Menu))
    val entry by nav.currentBackStackEntryAsState(); val route = entry?.destination?.route ?: "home"
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(containerColor=Ink, bottomBar={ if(route in items.map{it.route}) BottomBar(items, route){nav.navigate(it){popUpTo("home"){saveState=true};launchSingleTop=true;restoreState=true}} }) { pad ->
            NavHost(navController=nav,startDestination="home",modifier=Modifier.padding(pad)) {
                composable("home"){HomeScreen{nav.navigate("details")}}
                composable("library"){LibraryScreen{nav.navigate("details")}}
                composable("updates"){UpdatesScreen()}
                composable("browse"){BrowseScreen()}
                composable("more"){MoreScreen{nav.navigate("settings")}}
                composable("settings"){SettingsScreen{nav.popBackStack()}}
                composable("details"){DetailsScreen({nav.popBackStack()}){nav.navigate("player")}}
                composable("player"){PlayerScreen(null){nav.popBackStack()}}
            }
        }
    }
}

@Composable fun BottomBar(items:List<NavItem>, selected:String, onClick:(String)->Unit){ Surface(color=Surface.copy(.96f),shape=RoundedCornerShape(32.dp),tonalElevation=8.dp,modifier=Modifier.padding(16.dp).height(70.dp)){Row(Modifier.fillMaxSize().padding(6.dp),horizontalArrangement=Arrangement.SpaceEvenly){items.forEach{val on=it.route==selected;Box(Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(26.dp)).background(if(on) Brush.linearGradient(listOf(Mint,Sky)) else Brush.linearGradient(listOf(Color.Transparent,Color.Transparent))).clickable{onClick(it.route)},contentAlignment=Alignment.Center){Column(horizontalAlignment=Alignment.CenterHorizontally){Icon(it.icon,null,tint=if(on)Color(0xFF04121A) else Muted,modifier=Modifier.size(22.dp));Text(it.label,fontSize=11.sp,color=if(on)Color(0xFF04121A) else Muted,fontWeight=if(on)FontWeight.Bold else FontWeight.Normal)}}}}}}

@Composable fun Header(title:String, action:()->Unit={}){Row(Modifier.fillMaxWidth().padding(horizontal=20.dp,vertical=18.dp),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically){Text(title,fontSize=28.sp,fontWeight=FontWeight.ExtraBold);Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){IconButton(onClick=action){Icon(Icons.Default.Search,"بحث")};IconButton(onClick={}){Icon(Icons.Default.NotificationsNone,"الإشعارات")}}}}
@Composable fun Chip(text:String, active:Boolean=false, onClick:()->Unit={}){Surface(modifier=Modifier.clickable{onClick()},color=if(active)Mint else Surface,contentColor=if(active)Color(0xFF04121A) else Muted,shape=RoundedCornerShape(20.dp),border=if(active)null else BorderStroke(1.dp,Border)){Text(text,Modifier.padding(horizontal=14.dp,vertical=9.dp),fontSize=13.sp,fontWeight=if(active)FontWeight.Bold else FontWeight.Normal)}}
@Composable fun Section(title:String,onMore:()->Unit={}){Row(Modifier.fillMaxWidth().padding(horizontal=20.dp,vertical=12.dp),horizontalArrangement=Arrangement.SpaceBetween){Text(title,fontSize=18.sp,fontWeight=FontWeight.Bold);Text("عرض الكل",color=Mint,fontSize=13.sp,modifier=Modifier.clickable{onMore()})}}

@Composable fun HomeScreen(openDetails:()->Unit){LazyColumn(Modifier.fillMaxSize().background(Ink)){item{Row(Modifier.fillMaxWidth().padding(20.dp),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically){Row(verticalAlignment=Alignment.CenterVertically){Image(painterResource(com.animeslime.app.R.drawable.anime_slime_logo),"شعار Anime Slime",Modifier.size(38.dp).clip(RoundedCornerShape(12.dp)),contentScale=ContentScale.Crop);Spacer(Modifier.width(10.dp));Text("Anime Slime",fontSize=22.sp,fontWeight=FontWeight.ExtraBold,color=Mint)};Row{IconButton({}){Icon(Icons.Default.Search,"بحث")};IconButton({}){Icon(Icons.Default.NotificationsNone,"الإشعارات")}}}};item{Row(Modifier.horizontalScroll(rememberScrollState()).padding(horizontal=20.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)){Chip("كل المصادر",true);Chip("NovaStream");Chip("ArabSub");Chip("Kuroi")}};item{HeroCard(openDetails)};item{Section("تابع المشاهدة")};item{Row(Modifier.horizontalScroll(rememberScrollState()).padding(horizontal=20.dp),horizontalArrangement=Arrangement.spacedBy(12.dp)){listOf("سيف العاصفة" to "الحلقة 7 · متبقي 12 د","مدرسة الأرواح" to "الحلقة 3 · متبقي 18 د","كوكب الحدود" to "الحلقة 10 · متبقي 5 د").forEach{WatchCard(it.first,it.second,openDetails)}}};item{Section("جديد في مكتبتك")};items(listOf("كوكب الحدود" to "الحلقة 5 · قبل ساعتين","ملاذ النجوم" to "الحلقة 12 · اليوم")){NewRow(it.first,it.second)}}}
@Composable fun HeroCard(open:()->Unit){Box(Modifier.padding(horizontal=20.dp,vertical=14.dp).fillMaxWidth().height(210.dp).clip(RoundedCornerShape(24.dp)).background(Brush.linearGradient(listOf(Color(0xFF7C3AED),Color(0xFF1E1B4B)))).clickable{open()}){Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent,Color.Black.copy(.8f)))));Column(Modifier.align(Alignment.BottomEnd).padding(16.dp)){Text("مميز اليوم · NovaStream",fontSize=12.sp,color=Mint);Text("ظل الفجر",fontSize=26.sp,fontWeight=FontWeight.ExtraBold);Text("أكشن · فانتازيا · 12 حلقة",fontSize=12.sp,color=Muted);Spacer(Modifier.height(10.dp));Button(onClick=open,colors=ButtonDefaults.buttonColors(containerColor=Mint,contentColor=Color(0xFF04121A))){Icon(Icons.Default.PlayArrow,null);Text("شاهد الآن",fontWeight=FontWeight.Bold)}}}}
@Composable fun WatchCard(title:String,sub:String,open:()->Unit){Column(Modifier.width(168.dp).clickable{open()}){Box(Modifier.fillMaxWidth().height(94.dp).clip(RoundedCornerShape(14.dp)).background(Brush.linearGradient(listOf(Mint,Color(0xFF0C2A4A)))),contentAlignment=Alignment.Center){Icon(Icons.Default.PlayArrow,"تشغيل",Modifier.size(30.dp))};Text(title,fontWeight=FontWeight.Bold,modifier=Modifier.padding(top=6.dp));Text(sub,color=Muted,fontSize=12.sp)}}
@Composable fun NewRow(title:String,sub:String){Surface(color=Surface,shape=RoundedCornerShape(18.dp),border=BorderStroke(1.dp,Border),modifier=Modifier.padding(horizontal=20.dp,vertical=5.dp)){Row(Modifier.padding(10.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(48.dp,64.dp).clip(RoundedCornerShape(10.dp)).background(Brush.linearGradient(listOf(Color(0xFFEC4899),Color(0xFF4A1042)))));Column(Modifier.weight(1f).padding(horizontal=12.dp)){Text(title,fontWeight=FontWeight.Bold);Text(sub,color=Muted,fontSize=12.sp)};IconButton(onClick={}){Icon(Icons.Default.Download,"تنزيل",tint=Mint)}}}}
