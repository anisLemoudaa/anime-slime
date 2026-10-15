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
data class SearchUiState(val loading:Boolean=false,val query:String="",val results:List<SourceAnime> = emptyList(),val error:String?=null)
@HiltViewModel class SearchViewModel @Inject constructor(private val registry:CatalogRegistry):ViewModel(){private val _state=MutableStateFlow(SearchUiState());val state:StateFlow<SearchUiState> = _state.asStateFlow();fun search(query:String){_state.value=_state.value.copy(query=query,loading=true,error=null);viewModelScope.launch{runCatching{registry.searchAll(query)}.onSuccess{_state.value=SearchUiState(query=query,results=it)}.onFailure{_state.value=SearchUiState(query=query,error=it.message)}}}}
