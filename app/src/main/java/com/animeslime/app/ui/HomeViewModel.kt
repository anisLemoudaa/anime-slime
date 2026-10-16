package com.animeslime.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animeslime.app.data.CatalogRegistry
import com.animeslime.app.data.SourceAnime
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeSource(val id: String, val name: String)
data class HomeUiState(
    val loading: Boolean = true,
    val items: List<SourceAnime> = emptyList(),
    val sources: List<HomeSource> = emptyList(),
    val selectedSource: String = ALL_SOURCES,
    val error: String? = null,
)

private const val ALL_SOURCES = "all"

@HiltViewModel
class HomeViewModel @Inject constructor(private val registry: CatalogRegistry) : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState(sources = sourceOptions()))
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init { refresh() }

    fun selectSource(id: String) {
        if (id == _state.value.selectedSource) return
        _state.value = _state.value.copy(selectedSource = id)
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val selected = _state.value.selectedSource
            _state.value = _state.value.copy(loading = true, error = null, sources = sourceOptions())
            val catalogs = if (selected == ALL_SOURCES) registry.all() else registry.all().filter { it.id == selected }
            val results = catalogs.map { catalog -> catalog.latest().getOrElse { emptyList() } }.flatten()
            val error = if (results.isEmpty()) "لم تُرجع المصادر المثبتة أي أعمال" else null
            _state.value = _state.value.copy(loading = false, items = results, error = error)
        }
    }

    private fun sourceOptions(): List<HomeSource> = listOf(HomeSource(ALL_SOURCES, "الكل")) +
        registry.all().map { HomeSource(it.id, it.name) }
}
