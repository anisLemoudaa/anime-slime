package com.animeslime.app.ui
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animeslime.domain.LibraryRepository
import com.animeslime.app.data.Anime
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
@HiltViewModel class LibraryViewModel @Inject constructor(private val repository:LibraryRepository):ViewModel(){
 val items:StateFlow<List<Anime>> = repository.observeLibrary().map { entries -> entries.map { entry -> Anime(sourceId=entry.sourceId.toString(),url=entry.anime.url,title=entry.anime.title,cover=entry.anime.thumbnailUrl,description=entry.anime.description,inLibrary=true) } }.stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 fun remove(item:Anime)=viewModelScope.launch{repository.remove(item.url,item.sourceId.toLongOrNull() ?: 0L)}
}
