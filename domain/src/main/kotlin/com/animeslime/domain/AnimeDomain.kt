package com.animeslime.domain

import com.animeslime.sourceapi.*
import kotlinx.coroutines.flow.Flow

interface AnimeSourceRepository {
    fun observeSources(): Flow<List<AnimeSource>>
    fun getSource(id: Long): AnimeSource?
    suspend fun search(sourceId: Long, query: String, page: Int = 1): AnimePage
    suspend fun popular(sourceId: Long, page: Int = 1): AnimePage
    suspend fun latest(sourceId: Long, page: Int = 1): AnimePage
    suspend fun details(sourceId: Long, anime: SAnime): SAnime
    suspend fun episodes(sourceId: Long, anime: SAnime): List<SEpisode>
    suspend fun videos(sourceId: Long, episode: SEpisode): List<Video>
}

interface LibraryRepository {
    fun observeLibrary(): Flow<List<LibraryEntry>>
    fun observeHistory(): Flow<List<HistoryEntry>>
    suspend fun add(anime: SAnime, sourceId: Long)
    suspend fun remove(animeUrl: String, sourceId: Long)
    suspend fun saveProgress(sourceId: Long, animeUrl: String, episodeUrl: String, positionMs: Long, durationMs: Long)
}

data class LibraryEntry(val sourceId: Long, val anime: SAnime, val unreadEpisodes: Int = 0)
data class HistoryEntry(val sourceId: Long, val anime: SAnime, val episode: SEpisode, val positionMs: Long, val durationMs: Long)

class SearchAnime(private val sources: AnimeSourceRepository) {
    suspend fun await(sourceId: Long, query: String): AnimePage = sources.search(sourceId, query)
}
class GetAnimeDetails(private val sources: AnimeSourceRepository) {
    suspend fun await(sourceId: Long, anime: SAnime): SAnime = sources.details(sourceId, anime)
}
class GetAnimeEpisodes(private val sources: AnimeSourceRepository) {
    suspend fun await(sourceId: Long, anime: SAnime): List<SEpisode> = sources.episodes(sourceId, anime)
}
