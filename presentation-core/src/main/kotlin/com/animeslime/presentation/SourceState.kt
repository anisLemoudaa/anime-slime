package com.animeslime.presentation

import com.animeslime.domain.AnimeSourceRepository
import com.animeslime.sourceapi.AnimePage
import com.animeslime.sourceapi.SAnime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SourceUiState(val loading: Boolean = false, val page: AnimePage? = null, val error: String? = null)

class SourceViewModel(private val repository: AnimeSourceRepository, private val scope: CoroutineScope) {
    private val _state = MutableStateFlow(SourceUiState())
    val state: StateFlow<SourceUiState> = _state.asStateFlow()
    fun search(sourceId: Long, query: String) = scope.launch {
        _state.value = SourceUiState(loading = true)
        runCatching { repository.search(sourceId, query) }
            .onSuccess { _state.value = SourceUiState(page = it) }
            .onFailure { _state.value = SourceUiState(error = it.message ?: "تعذر الاتصال بالمصدر") }
    }
    fun latest(sourceId: Long) = scope.launch {
        _state.value = SourceUiState(loading = true)
        runCatching { repository.latest(sourceId) }
            .onSuccess { _state.value = SourceUiState(page = it) }
            .onFailure { _state.value = SourceUiState(error = it.message ?: "تعذر الاتصال بالمصدر") }
    }
}
