package com.animeslime.app.ui
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animeslime.app.data.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
data class HomeUiState(val loading:Boolean=true,val items:List<SourceAnime> = emptyList(),val error:String?=null)
@HiltViewModel class HomeViewModel @Inject constructor(private val registry:CatalogRegistry):ViewModel(){private val _state=MutableStateFlow(HomeUiState());val state:StateFlow<HomeUiState> = _state.asStateFlow();init{refresh()};fun refresh(){viewModelScope.launch{_state.value=HomeUiState();runCatching{registry.latestAll()}.onSuccess{_state.value=HomeUiState(false,it)}.onFailure{_state.value=HomeUiState(false,error=it.message)}}}}
