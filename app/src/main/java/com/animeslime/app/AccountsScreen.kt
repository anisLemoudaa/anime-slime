package com.animeslime.app

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animeslime.app.data.*
import com.animeslime.app.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject

@HiltViewModel
class AccountsViewModel @Inject constructor(private val prefs:AppPreferences, private val oauth:OAuthManager):ViewModel(){
 val tokens=prefs.trackerTokens.stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),TrackerTokens())
 var message by mutableStateOf<String?>(null)
 init { viewModelScope.launch { OAuthBus.events.collect { event ->
  if(event.error!=null) message="فشل تسجيل الدخول: ${event.error}"
  else oauth.exchange(event){res->res.onSuccess{json->
   val obj=JSONObject(json);val token=obj.optString("access_token");val refresh=obj.optString("refresh_token");viewModelScope.launch{prefs.setTrackerToken(event.provider,token,refresh);message="تم تسجيل الدخول إلى ${if(event.provider=="mal")"MAL" else "AniList"}"
   }
  }.onFailure{message=it.message}}
 } } }
 fun login(provider:String)=oauth.start(provider)
 fun logout(provider:String)=viewModelScope.launch{prefs.clearTrackerToken(provider)}
}

@Composable fun AccountsScreen(back:()->Unit){
 val vm:AccountsViewModel=hiltViewModel();val tokens by vm.tokens.collectAsState()
 Column(Modifier.fillMaxSize().padding(20.dp)){Text("الحسابات والمزامنة",style=MaterialTheme.typography.headlineSmall);Spacer(Modifier.height(18.dp));AccountRow("AniList",tokens.anilist!=null,{vm.login("anilist")}){vm.logout("anilist")};AccountRow("MyAnimeList",tokens.mal!=null,{vm.login("mal")}){vm.logout("mal")};vm.message?.let{Text(it,color=Mint,modifier=Modifier.padding(top=18.dp))};Spacer(Modifier.height(20.dp));Text("بعد تسجيل الدخول ستتم مزامنة حالة الحلقة والتقدم عند تشغيلها.",color=Muted);TextButton(back){Text("رجوع")}}
}
@Composable private fun AccountRow(name:String,connected:Boolean,login:()->Unit,logout:()->Unit){Surface(color=Surface,modifier=Modifier.fillMaxWidth().padding(vertical=6.dp)){Row(Modifier.padding(16.dp),horizontalArrangement=Arrangement.SpaceBetween){Column(Modifier.weight(1f)){Text(name);Text(if(connected)"متصل" else "غير متصل",color=if(connected)Mint else Muted)};Button(onClick=if(connected)logout else login){Text(if(connected)"فصل" else "تسجيل الدخول")}}}}
