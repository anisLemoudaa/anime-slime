package com.animeslime.app.ui
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animeslime.app.data.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
@HiltViewModel class SettingsViewModel @Inject constructor(private val prefs:AppPreferences):ViewModel(){val settings=prefs.settings.stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),Settings(true,"dark","cyan","ar",true,true,10,true,false,true,true));fun autoIntro(v:Boolean)=viewModelScope.launch{prefs.setAutoIntro(v)};fun autoNext(v:Boolean)=viewModelScope.launch{prefs.setAutoNext(v)};fun wifiOnly(v:Boolean)=viewModelScope.launch{prefs.setWifiOnly(v)};fun notifications(v:Boolean)=viewModelScope.launch{prefs.setNotifications(v)};fun accent(v:String)=viewModelScope.launch{prefs.setAccent(v)}}
