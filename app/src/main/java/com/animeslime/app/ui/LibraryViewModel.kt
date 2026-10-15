package com.animeslime.app.ui
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animeslime.app.data.Anime
import com.animeslime.app.data.AnimeDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
@HiltViewModel class LibraryViewModel @Inject constructor(private val dao:AnimeDao):ViewModel(){val items:StateFlow<List<Anime>> =dao.library().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList());fun remove(item:Anime)=viewModelScope.launch{dao.setLibrary(item.id,false)}}
