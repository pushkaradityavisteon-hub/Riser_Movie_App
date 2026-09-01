# Riser Movie App

An Android application built as a learning project to demonstrate real-world Android concepts including multi-process architecture, AIDL-based IPC, Clean Architecture, Hilt DI, Room persistence, and Jetpack Compose UI.

---

## Architecture

The project follows **Clean Architecture** with 3 Gradle modules:

| Module | Responsibility |
|--------|---------------|
| `:Domain` | Pure Kotlin. `Movie` domain model + `IMovieRepository` interface. Zero Android imports. |
| `:Data` | Room DB, Retrofit, GSON, Repository implementation, SharedPreferences, Hilt DI modules. |
| `:app` | Compose UI, ViewModels, Services, AIDL clients, Navigation. |

**Pattern:** MVVM with StateFlow. ViewModels depend on abstractions (interfaces), never concrete classes.

---

## Features

- **Movie listing** — fetches popular movies from TMDB API, caches in Room DB
- **Favourites** — add/remove favourites via AIDL bound service running in a separate process
- **Poster download** — download movie posters to device gallery via AIDL bound service in a separate process
- **Login/session** — basic client-side session using SharedPreferences
- **Offline support** — movies cached in Room, displayed even without network

---

## Multi-Process Architecture

The app runs across **3 OS processes**:

```
com.example.movie_app           → main process (UI, ViewModels, Hilt)
com.example.movie_app:favourites → FavouritesService (AIDL bound)
com.example.movie_app:download   → DownloadService (AIDL bound)
```

Communication between processes uses **AIDL + Binder IPC** with `oneway` callbacks for non-blocking notifications back to the main process.

---

## Tech Stack

| Library | Purpose |
|---------|---------|
| Jetpack Compose | UI framework |
| Hilt | Dependency injection |
| Room | Local SQLite database |
| Retrofit + GSON | HTTP client + JSON parsing |
| OkHttp Logging Interceptor | Network request/response logging |
| Glide Compose | Image loading |
| Navigation Compose | Screen navigation |
| Kotlin Coroutines + Flow | Async + reactive data streams |
| AIDL | Cross-process interface definition |
| KSP | Compile-time code generation (Room + Hilt) |

---

## Dependency Injection

3 Hilt modules:

- **`DatabaseModule`** — provides `AppDatabase` and `MovieDao` as `@Singleton`
- **`NetworkModule`** — provides `MovieApi` via Retrofit as `@Singleton`
- **`RepositoryModule`** — `@Binds` `MovieRepository` → `IMovieRepository`
- **`IpcModule`** — `@Binds` `FavouritesClient` → `IFavouritesClient`, `DownloadClient` → `IDownloadClient`

---

## SOLID Principles

| Principle | Evidence |
|-----------|---------|
| **S** — Single Responsibility | Each class has one job. `MovieSyncService` only syncs. `MovieDao` only queries. |
| **O** — Open/Closed | `UiState`, `DownloadState`, `Screen` are sealed classes — extend by adding subclasses. |
| **L** — Liskov Substitution | `MovieRepository` fully satisfies `IMovieRepository`. Wired via `@Binds`. |
| **I** — Interface Segregation | `IMovieRepository` has 2 methods. AIDL interfaces are small and focused. |
| **D** — Dependency Inversion | All ViewModels and MainActivity depend on interfaces, not concrete classes. |

---

## Known Issues / Dev Notes

- **SSL validation disabled** in `NetworkModule` — trust-all `X509TrustManager` was added as a dev workaround. Must be replaced before production.
- **Password stored in plaintext** in SharedPreferences — dev only, no real auth backend.
- **Cancel download** does not cancel the running OkHttp coroutine — only clears the dedup guard.
- **Favourites not persisted across app uninstall** — SharedPreferences in `:favourites` process is cleared on uninstall.

---

## Bug Fix — Favourites Reset on App Backgrounding

**Root cause:** `unbindService()` was called in `onStop()`, dropping the last Binder reference to `:favourites` process. Android's Low Memory Killer reclaimed the process. On return, `FavouritesService` restarted with empty in-memory state.

**Fix (commit `ebb1f71`):**
1. Moved `bind()`/`unbind()` from `onStart/onStop` to `onCreate/onDestroy` — connection stays alive for full activity lifetime.
2. Added SharedPreferences persistence to `FavouritesService` — `loadFromPrefs()` on `onCreate()`, `saveToPrefs()` on every mutation.

---

## Network Fix for Physical Devices

If the app shows **"Failed to load movies"** on a physical device using mobile data, your carrier may be blocking DNS for `api.themoviedb.org`.

**Fix:** Go to Settings → Connections → More connection settings → Private DNS → set to `dns.google`

Alternatively use Wi-Fi or the Android Emulator.

---

## Project Setup

1. Clone the repo
2. Add your TMDB API key to `local.properties`:
   ```
   TMDB_API_KEY=your_key_here
   ```
3. Build and run on API 24+
