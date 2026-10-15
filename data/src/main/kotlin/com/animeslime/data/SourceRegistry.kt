package com.animeslime.data

import com.animeslime.domain.AnimeSourceRepository
import com.animeslime.sourceapi.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SourceRegistry(initial: List<AnimeSource> = emptyList()) : AnimeSourceRepository {
    private val sources = MutableStateFlow(initial.distinctBy { it.id })
    fun install(source: AnimeSource) { sources.value = (sources.value.filterNot { it.id == source.id } + source).sortedBy { it.name } }
    fun uninstall(sourceId: Long) { sources.value = sources.value.filterNot { it.id == sourceId } }
    override fun observeSources(): Flow<List<AnimeSource>> = sources.asStateFlow()
    override fun getSource(id: Long): AnimeSource? = sources.value.firstOrNull { it.id == id }
    private fun source(id: Long): AnimeSource = getSource(id) ?: throw SourceException("المصدر غير مثبت: $id")
    override suspend fun search(sourceId: Long, query: String, page: Int) = source(sourceId).search(query, page)
    override suspend fun popular(sourceId: Long, page: Int) = source(sourceId).popular(page)
    override suspend fun latest(sourceId: Long, page: Int) = source(sourceId).latest(page)
    override suspend fun details(sourceId: Long, anime: SAnime) = source(sourceId).details(anime)
    override suspend fun episodes(sourceId: Long, anime: SAnime) = source(sourceId).episodes(anime)
    override suspend fun videos(sourceId: Long, episode: SEpisode): List<Video> = source(sourceId).hosts(episode).flatMap { source(sourceId).videos(it) }
}
