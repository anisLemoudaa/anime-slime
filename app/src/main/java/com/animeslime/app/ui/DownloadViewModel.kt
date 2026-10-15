package com.animeslime.app.ui
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animeslime.app.data.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel class DownloadViewModel @Inject constructor(private val dao:DownloadDao,@ApplicationContext private val context:Context):ViewModel(){
 val items:StateFlow<List<DownloadRow>> = dao.rows().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 fun cancel(item:DownloadRow)=viewModelScope.launch{DownloadQueue.cancel(context,item.episodeId);dao.update(item.episodeId,"CANCELLED",item.progress,item.error,item.filePath)}
 fun retry(item:DownloadRow)=viewModelScope.launch{dao.upsert(DownloadItem(item.episodeId,item.url,"QUEUED",item.progress,null,null));DownloadQueue.enqueue(context,item.episodeId,item.url)}
 fun remove(item:DownloadRow)=viewModelScope.launch{DownloadQueue.cancel(context,item.episodeId);dao.delete(item.episodeId)}
}
