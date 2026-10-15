package com.animeslime.app.ui
import android.content.Context
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
@HiltViewModel class SourceDetailsViewModel @Inject constructor(private val registry:CatalogRegistry,private val resolvers:ServerResolverRegistry,private val animeDao:AnimeDao,private val episodeDao:EpisodeDao):ViewModel(){
 private val _state=MutableStateFlow(SourceDetailsUiState());val state:StateFlow<SourceDetailsUiState> = _state.asStateFlow()
 fun load(url:String){viewModelScope.launch{registry.byUrl(url).details(url).onSuccess{d->val resolved=d.copy(episodes=d.episodes.map{episode->episode.copy(videoLinks=resolvers.resolve(episode.url).getOrDefault(emptyList()).map{it.url})});_state.value=SourceDetailsUiState(false,resolved)}.onFailure{_state.value=SourceDetailsUiState(false,error=it.message)}}}
 fun addToLibrary(){_state.value.details?.let{d->viewModelScope.launch{animeDao.upsert(Anime(sourceId=d.anime.sourceId,url=d.anime.url,title=d.anime.title,cover=d.anime.cover,description=d.anime.description,inLibrary=true))}}}
 fun download(episode:SourceEpisode,context:Context){val direct=episode.videoLinks.firstOrNull{it.contains(".mp4",true)||it.contains(".mkv",true)||it.contains(".zip",true)}?:return;viewModelScope.launch{val d=_state.value.details?:return@launch;val animeId=animeDao.upsert(Anime(sourceId=d.anime.sourceId,url=d.anime.url,title=d.anime.title,cover=d.anime.cover,description=d.anime.description,inLibrary=true));episodeDao.upsertAll(listOf(Episode(animeId=animeId,url=episode.url,number=episode.number,name=episode.title,videoUrl=direct)));val stored=episodeDao.byAnimeAndNumber(animeId,episode.number)?:return@launch;DownloadQueue.enqueue(context,stored.id,direct)}}
}
