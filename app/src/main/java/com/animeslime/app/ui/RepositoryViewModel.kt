package com.animeslime.app.ui
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animeslime.app.data.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class RepositoryUiState(val loading:Boolean=false,val repositories:List<Repository> = emptyList(),val extensions:List<Extension> = emptyList(),val error:String?=null,val message:String?=null)
@HiltViewModel class RepositoryViewModel @Inject constructor(private val repoDao:RepositoryDao,private val extensionDao:ExtensionDao,private val service:RepositoryService):ViewModel(){
 private val events=MutableStateFlow(RepositoryUiState())
 val state:StateFlow<RepositoryUiState> = combine(repoDao.all(),extensionDao.all(),events){repos,extensions,e->e.copy(repositories=repos,extensions=extensions)}.stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),RepositoryUiState())
 fun add(url:String){viewModelScope.launch{events.update{it.copy(loading=true,error=null,message=null)};service.fetch(url).onSuccess{idx->val repoId=repoDao.upsert(Repository(name=idx.name,url=url,extensionCount=idx.extensions.size,lastFetched=System.currentTimeMillis()));extensionDao.upsertAll(idx.extensions.map{m->Extension(pkg=m.pkg,name=m.name,lang=m.lang,version=m.version,repoId=repoId,adapter=m.adapter,baseUrl=m.baseUrl,manifestUrl=m.manifestUrl)});events.update{it.copy(loading=false,message="تمت فهرسة ${idx.extensions.size} إضافة")}}.onFailure{e->events.update{it.copy(loading=false,error=e.message?:"فشل قراءة فهرس المستودع")}}}}
 fun refresh(repo:Repository)=add(repo.url)
 fun toggle(repo:Repository)=viewModelScope.launch{repoDao.setEnabled(repo.id,!repo.enabled)}
 fun remove(repo:Repository)=viewModelScope.launch{repoDao.delete(repo.id)}
 fun install(ext:Extension)=viewModelScope.launch{extensionDao.setInstalled(ext.pkg,true);events.update{it.copy(message="تم تثبيت ${ext.name}" )}}
 fun uninstall(ext:Extension)=viewModelScope.launch{extensionDao.setInstalled(ext.pkg,false)}
 fun toggleExtension(ext:Extension)=viewModelScope.launch{extensionDao.setEnabled(ext.pkg,!ext.enabled)}
}
