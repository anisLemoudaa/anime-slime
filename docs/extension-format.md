# Anime Slime extension index

The repository screen now parses and stores extension manifests locally. A repository may expose either an object with `name` and `extensions`, or a root array. Each extension can declare:

```json
{
  "pkg": "com.example.anime.source",
  "name": "Example Arabic",
  "lang": "ar",
  "version": "1.0.0",
  "adapter": "animelek",
  "baseUrl": "https://example.org/"
}
```

The app supports repository refresh, local persistence, install/uninstall, enable/disable, and a visible unsupported-adapter state. Built-in adapters are deliberately whitelisted; the next source phase will connect adapter IDs to the common `CatalogSource` contract and add independent resolvers. Arbitrary APK code is not loaded from a URL, which prevents an untrusted repository from executing code inside the app.
