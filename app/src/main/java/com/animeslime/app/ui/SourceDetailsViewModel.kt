package com.animeslime.app.ui
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animeslime.app.data.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SourceDetailsUiState(val loading:Boolean=true,val details:SourceDetails?=null,val error:String?=null)
@HiltViewModel class SourceDetailsViewModel @Inject constructor(private val source:AnimeLekCatalog,private val animeDao:AnimeDao):ViewModel(){
 private val _state=MutableStateFlow(SourceDetailsUiState());val state:StateFlow<SourceDetailsUiState> = _state.asStateFlow()
 fun load(url:String){viewModelScope.launch{source.details(url).onSuccess{_state.value=SourceDetailsUiState(false,it)}.onFailure{_state.value=SourceDetailsUiState(false,error=it.message)}}}
 fun addToLibrary(){_state.value.details?.let{d->viewModelScope.launch{animeDao.upsert(Anime(sourceId="animelek",url=d.anime.url,title=d.anime.title,cover=d.anime.cover,description=d.anime.description,inLibrary=true))}}}
}
