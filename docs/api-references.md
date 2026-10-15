# API references

- AniList API docs: https://docs.anilist.co/ — GraphQL API for reading and mutating anime/media lists.
- AniList legacy documentation: https://anilist.gitbook.io/anilist-apiv2-docs — confirms GraphQL endpoint model and authenticated list operations.
- MyAnimeList API v2: https://myanimelist.net/apiconfig/references/api/v2 — OAuth2 authorization, `/v2/anime/{anime_id}/my_list_status` update, and user list endpoints.
- Android Media3 ExoPlayer: https://developer.android.com/media/media3/exoplayer — Player supports play, pause, seek and track information; ExoPlayer is the default implementation.
- Android Media3 overview: https://developer.android.com/media/media3 — MediaSession exposes playback externally and supports background controls.

The app does not include client secrets. OAuth credentials must be supplied through local or CI configuration before enabling a production tracker login.
