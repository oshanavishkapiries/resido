# Resido Data & Repository Layer

This directory contains the centralized data access layer for the Resido application. It isolates mock/test logic and models from the UI/screen code, allowing for an easy, seamless swap-in of a real production backend.

## Structure

- **`models/`**: Domain models (e.g., `User`, `Property`, `Conversation`, `Message`, `NotificationItem`) used throughout the app.
- **`repository/`**: Clean repository interfaces detailing all the capabilities and actions required by the screens.
- **`mock/`**: Complete, stateful in-memory implementations of the repository interfaces powered by a single source of truth seed (`MockDataSeed.kt`).

## How to Connect a Real Backend

To connect a real backend (e.g., Firebase, Firestore, REST API, or GraphQL):

1. **Create real repository implementations**: Create new Kotlin classes implementing the respective interfaces in the `com.example.data.repository` package:
   - For example, `class ApiPropertyRepository(private val apiService: MyApiService) : PropertyRepository { ... }`

2. **Configure/Provision them in the Service Locator**: Open `ResidoServiceLocator.kt` and swap out the mock assignments with your real implementations:
   ```kotlin
   object ResidoServiceLocator {
       // Replace the Mock repositories with Api/Firebase ones:
       val userRepository: UserRepository = ApiUserRepository(...)
       val propertyRepository: PropertyRepository = ApiPropertyRepository(...)
       ...
   }
   ```

Because every screen and component references `ResidoServiceLocator` and is coded against the **repository interfaces** (not the mock classes), **no screen or UI code will need to change!**
