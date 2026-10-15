# Animelek source adapter notes

The adapter targets the public pages at `https://animelek.cc/home` and searches with `https://animelek.cc/?s=<query>`. The current site uses `.anime-card-container` cards, an `.anime-card-title` title link, and `#EpList1 .CSB` episode selectors. Episode pages expose server identifiers in `#ServerList1` and may rely on JavaScript; the app therefore opens the source page in an in-app WebView when no direct media URL is available. The source can change its markup or server resolver at any time, so the adapter must be maintained independently from the app UI.

Reference project: https://github.com/aniyomiorg/aniyomi.git
