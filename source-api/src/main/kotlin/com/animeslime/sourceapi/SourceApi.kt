package com.animeslime.sourceapi

/** Contract for installable Anime Slime sources, inspired by AniYomi's AnimeSource API. */
interface AnimeSource {
    val id: Long
    val name: String
    val lang: String
    val supportsLatest: Boolean get() = true
    suspend fun popular(page: Int = 1): AnimePage
    suspend fun latest(page: Int = 1): AnimePage
    suspend fun search(query: String, page: Int = 1, filters: List<AnimeFilter> = emptyList()): AnimePage
    suspend fun details(anime: SAnime): SAnime
    suspend fun episodes(anime: SAnime): List<SEpisode>
    suspend fun hosts(episode: SEpisode): List<Hoster> = emptyList()
    suspend fun videos(hoster: Hoster): List<Video> = emptyList()
}

interface AnimeCatalogueSource : AnimeSource

data class SAnime(
    val url: String,
    val title: String,
    val thumbnailUrl: String? = null,
    val backgroundUrl: String? = null,
    val description: String? = null,
    val genres: List<String> = emptyList(),
    val status: AnimeStatus = AnimeStatus.UNKNOWN,
    val season: Double = 0.0,
)

data class SEpisode(
    val url: String,
    val name: String,
    val number: Float,
    val dateUpload: Long = 0L,
    val summary: String? = null,
    val hosters: List<Hoster> = emptyList(),
)

data class Hoster(val name: String, val url: String)
data class Video(val url: String, val quality: String, val referer: String? = null, val mimeType: String? = null)
data class AnimePage(val items: List<SAnime>, val hasNextPage: Boolean)
data class AnimeFilter(val key: String, val value: String)

enum class AnimeStatus { UNKNOWN, ONGOING, COMPLETED, UPCOMING, CANCELLED }

class SourceException(message: String, cause: Throwable? = null) : Exception(message, cause)
