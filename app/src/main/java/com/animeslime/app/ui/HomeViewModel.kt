package com.animeslime.app.ui
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animeslime.app.data.AnimeLekSource
import com.animeslime.app.data.SourceAnime
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
data class HomeUiState(val loading:Boolean=true,val items:List<SourceAnime> = emptyList(),val error:String?=null)
@HiltViewModel class HomeViewModel @Inject constructor(private val source:AnimeLekSource):ViewModel(){private val _state=MutableStateFlow(HomeUiState());val state:StateFlow<HomeUiState> =_state.asStateFlow();init{refresh()};fun refresh(){viewModelScope.launch{_state.value=HomeUiState();source.latest().onSuccess{_state.value=HomeUiState(false,it)}.onFailure{_state.value=HomeUiState(false,error=it.message)}}}}
