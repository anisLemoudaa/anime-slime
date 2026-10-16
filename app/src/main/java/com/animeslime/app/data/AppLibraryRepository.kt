package com.animeslime.app.data

import com.animeslime.domain.HistoryEntry
import com.animeslime.domain.LibraryEntry
import com.animeslime.domain.LibraryRepository
import com.animeslime.sourceapi.SAnime
import com.animeslime.sourceapi.SEpisode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class AppLibraryRepository(
    private val animeDao: AnimeDao,
    private val episodeDao: EpisodeDao,
    private val historyDao: HistoryDao,
) : LibraryRepository {
    override fun observeLibrary(): Flow<List<LibraryEntry>> = animeDao.library().map { list ->
        list.map { it.toDomain() }
    }

    override fun observeHistory(): Flow<List<HistoryEntry>> = historyDao.recent().map { emptyList() }

    override suspend fun add(anime: SAnime, sourceId: Long) {
        animeDao.upsert(anime.toEntity(sourceId.toString(), true))
    }

    override suspend fun remove(animeUrl: String, sourceId: Long) {
        animeDao.bySourceAndUrl(sourceId.toString(), animeUrl)?.let { animeDao.setLibrary(it.id, false) }
    }

    override suspend fun saveProgress(sourceId: Long, animeUrl: String, episodeUrl: String, positionMs: Long, durationMs: Long) {
        val anime = animeDao.bySourceAndUrl(sourceId.toString(), animeUrl) ?: return
        val episode = episodeDao.byAnimeAndUrl(anime.id, episodeUrl) ?: return
        episodeDao.saveProgress(episode.id, positionMs >= durationMs - 5_000 && durationMs > 0, positionMs, durationMs)
        historyDao.save(History(episode.id, progressMs = positionMs))
    }

    private fun Anime.toDomain() = LibraryEntry(
        sourceId = sourceId.toLongOrNull() ?: 0L,
        anime = SAnime(url, title, cover, description = description, genres = genres.orEmpty().split(",").filter(String::isNotBlank)),
    )

    private fun SAnime.toEntity(source: String, library: Boolean) = Anime(
        sourceId = source, url = url, title = title, cover = thumbnailUrl,
        description = description, genres = genres.joinToString(","), inLibrary = library,
    )
}
