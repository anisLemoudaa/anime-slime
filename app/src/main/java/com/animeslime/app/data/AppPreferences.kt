package com.animeslime.app.data
import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.store by preferencesDataStore("anime_slime_settings")
class AppPreferences(private val context:Context){
 private object K{val firstRun=booleanPreferencesKey("first_run");val dark=stringPreferencesKey("theme");val accent=stringPreferencesKey("accent");val language=stringPreferencesKey("language");val autoIntro=booleanPreferencesKey("auto_intro");val autoNext=booleanPreferencesKey("auto_next");val skipSeconds=intPreferencesKey("skip_seconds");val wifiOnly=booleanPreferencesKey("wifi_only");val stealth=booleanPreferencesKey("stealth");val hiddenAdult=booleanPreferencesKey("hidden_adult");val notifications=booleanPreferencesKey("notifications")}
 val settings=context.store.data.map{p->Settings(p[K.firstRun]?:true,p[K.dark]?:"dark",p[K.accent]?:"cyan",p[K.language]?:"ar",p[K.autoIntro]?:true,p[K.autoNext]?:true,p[K.skipSeconds]?:10,p[K.wifiOnly]?:true,p[K.stealth]?:false,p[K.hiddenAdult]?:true,p[K.notifications]?:true)}
 suspend fun setFirstRun(value:Boolean)=context.store.edit{it[K.firstRun]=value}
 suspend fun setStealth(value:Boolean)=context.store.edit{it[K.stealth]=value}
 suspend fun setTheme(value:String)=context.store.edit{it[K.dark]=value}
 suspend fun setAutoIntro(value:Boolean)=context.store.edit{it[K.autoIntro]=value}
 suspend fun setAutoNext(value:Boolean)=context.store.edit{it[K.autoNext]=value}
 suspend fun setWifiOnly(value:Boolean)=context.store.edit{it[K.wifiOnly]=value}
 suspend fun setNotifications(value:Boolean)=context.store.edit{it[K.notifications]=value}
 suspend fun setAccent(value:String)=context.store.edit{it[K.accent]=value}
}
data class Settings(val firstRun:Boolean,val theme:String,val accent:String,val language:String,val autoIntro:Boolean,val autoNext:Boolean,val skipSeconds:Int,val wifiOnly:Boolean,val stealth:Boolean,val hiddenAdult:Boolean,val notifications:Boolean)
