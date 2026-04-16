# Sports Results Dashboard

An Android application that displays sports results, competitions, and matches with an offline-first approach.

## Overview

The application provides a dashboard where users can:
- Browse different sports.
- View live matches with real-time updates (simulated via polling/refresh).
- Filter prematch matches by time categories (Today, Tomorrow, Weekend, etc.).
- Access data even when offline, thanks to a local file-based caching system.

## Architecture

The project follows a clean architecture-inspired structure with a clear separation of concerns:

### 1. Presentation Layer (`presentation/`)
- **Jetpack Compose**: Used for building the UI. `DashboardScreen.kt` contains the main UI components.
- **ViewModel**: `SportsViewModel.kt` manages the UI state using `StateFlow`. It combines data from multiple repository sources to provide a unified `DashboardState`.

### 2. Domain Layer (`domain/`)
- **Models**: `Models.kt` defines the data structures for `Sport`, `Competition`, `Match`, and `MatchResult`.
- **Repository**: `SportsRepository.kt` acts as the single source of truth for data. It implements an "offline-first" strategy:
    1. Emits cached data immediately if available.
    2. Fetches fresh data from the network.
    3. Updates the local cache with the new data.
    4. Emits the fresh data.

### 3. Data Layer (`network/` and `FileCacheManager.kt`)
- **Networking**: `SportsApi.kt` defines the Retrofit interface for fetching data from a remote REST API.
- **Caching**: `FileCacheManager.kt` provides a generic way to save and load JSON data using the internal storage.

### 4. Dependency Injection (`di/`)
- **Koin**: Used for managing dependencies and providing them to the components. `AppModule.kt` defines how to provide `Retrofit`, `OkHttpClient`, `SportsRepository`, and `SportsViewModel`.

## Tool Chain & Libraries

- **Kotlin**: Primary programming language.
- **Jetpack Compose**: Modern toolkit for building native UI.
- **Retrofit & OkHttp**: For network requests.
- **Kotlinx Serialization**: For JSON parsing and serialization.
- **Koin**: Lightweight dependency injection framework.
- **Coil**: Image loading library with SVG support.
- **Coroutines & Flow**: For asynchronous programming and reactive data streams.

## Improvements

To further enhance the application, the following improvements could be considered:

- [ ] **Database Integration**: Replace the simple file-based cache with **Room** for more robust data persistence, complex queries, and better performance.
- [ ] **Error Handling**: Implement a more sophisticated error handling mechanism with user- [ ]friendly messages and retry options in the UI.
- [ ] **Pagination**: If the number of matches or competitions grows large, implement pagination to improve loading times and memory usage.
- [ ] **Unit & UI Testing**: Increase test coverage with unit tests for the ViewModel and Repository, and UI tests using Compose Test Rule.
- [ ] **Navigation**: Introduce the Jetpack Navigation component if more screens are added.
- [ ] **Theming**: Expand the Material3 theme with more customized colors, typography, and support for Dark Mode.
- [ ] **Localization**: Externalize all strings to `strings.xml` and provide translations for multiple languages.
- [ ] **Refresh Mechanism**: Add "Pull to Refresh" functionality to the dashboard.
- [ ] **Dependency Management**: Use Gradle Version Catalogs (already partially used) for better dependency version management across the project.
