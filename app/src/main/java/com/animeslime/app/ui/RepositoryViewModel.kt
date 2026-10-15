package com.animeslime.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animeslime.app.data.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class RepositoryUiState(val loading:Boolean=false,val repositories:List<Repository> = emptyList(),val error:String?=null,val message:String?=null)
@HiltViewModel class RepositoryViewModel @Inject constructor(private val dao:RepositoryDao,private val service:RepositoryService=RepositoryService()):ViewModel(){
 private val _state=MutableStateFlow(RepositoryUiState());val state:StateFlow<RepositoryUiState>=combine(dao.all(),_state){repos,s->s.copy(repositories=repos)}.stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),RepositoryUiState())
 fun add(url:String){viewModelScope.launch{_state.update{it.copy(loading=true,error=null,message=null)};service.fetch(url).onSuccess{idx->dao.upsert(Repository(name=idx.name,url=url,extensionCount=idx.extensions.size,lastFetched=System.currentTimeMillis()));_state.update{it.copy(loading=false,message="تمت إضافة ${idx.name} (${idx.extensions.size} إضافة)")}}.onFailure{e->_state.update{it.copy(loading=false,error=e.message?:"فشل التحقق من المستودع")}}}}
 fun refresh(repo:Repository)=add(repo.url)
 fun toggle(repo:Repository)=viewModelScope.launch{dao.setEnabled(repo.id,!repo.enabled)}
 fun remove(repo:Repository)=viewModelScope.launch{dao.delete(repo.id)}
}
