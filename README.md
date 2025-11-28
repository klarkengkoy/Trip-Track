# TripTrack (Work in Progress)

TripTrack is a comprehensive travel logging application for Android, designed to help users seamlessly record, organize, and manage their trips.  
Built with **Kotlin** and the **MVVM (Model-View-ViewModel)** architecture, the app emphasizes scalability, maintainability, and testability.  
It leverages modern Android development tools and best practices to deliver a robust, responsive, and intuitive user experience.

---

## ✨ Key Features

- **Modern & Reactive UI**  
  Built entirely with **Jetpack Compose** and **Material Design 3**, featuring a clean, responsive, and dynamic interface that reacts to state changes in real time.

- **Offline-First with Two-Way Sync**  
  Implements a fully functional **offline-first architecture** using **Room** and **Firebase Firestore**, ensuring data consistency and reliability.  
  Changes made offline are automatically synchronized to the cloud once connectivity is restored, and Firestore updates are seamlessly reflected in the local Room database.

- **Smooth & Efficient Performance**  
  Uses **Kotlin Coroutines** and **Flow** for concurrent background operations — from data fetching to sync handling — without blocking the main thread.

- **Flexible Authentication**  
  Secure sign-in and registration powered by **Firebase Authentication**, supporting **Google**, **Facebook**, and **Email/Password** logins.

- **Real-time Cloud Sync**  
  Trip data and user updates are synchronized instantly via **Firestore**, maintaining a consistent experience across devices.

- **Upcoming Features**
  - **Google Maps** for visualizing trips and destinations.  
  - **Retrofit** for integrating future REST APIs (e.g., travel data, weather).  
  - **Navigation 3** for unified, type-safe navigation across composables and future destinations.

---

## ⚙️ Technical Stack

### **Languages**
- **Kotlin** — Primary programming language for the entire application.

### **Core Android & UI**
- **Android Jetpack** — Ensures best practices and lifecycle-aware components.  
- **Jetpack Compose** — Declarative UI framework for building responsive layouts.  
- **Material Design 3** — Provides modern design components and consistent theming.  
- **Coil** — Lightweight and efficient image loading library.  
- **Android KTX** — Kotlin extensions that simplify Android APIs.

### **Architecture & Navigation**
- **MVVM (Model–View–ViewModel)** — For modular, testable, and maintainable structure.  
- **ViewModel + StateFlow** — Manages and emits UI state reactively.  
- **Navigation 3** — The latest generation of the Android Navigation library that unifies support for both **Views and Jetpack Compose**, providing type-safe destinations, simplified argument handling, and improved deep link support.

### **Asynchronous Operations**
- **Kotlin Coroutines** — For structured concurrency and non-blocking execution.  
- **Flow** — For reactive data streams and continuous updates.

### **Dependency Injection**
- **Hilt (Dagger)** — Streamlines dependency management and injection.

### **Data & Storage**
- **Room** — Local persistence layer for offline caching.  
- **Firebase Firestore** — Real-time NoSQL cloud database for remote data.  
- **Two-Way Sync Logic** — Custom synchronization layer between Room and Firestore, ensuring data parity and offline resilience.  
- **DataStore** — For storing lightweight key-value preferences.

### **Networking**
- **Retrofit (Planned)** — Type-safe HTTP client for future REST API integrations.

### **Authentication**
- **Firebase Authentication** — Provides secure, multi-provider user authentication.

### **Mapping**
- **Google Maps (Planned)** — Will enable map visualization of trips and markers.

---

## 🧩 Future Roadmap
- Integrate **Retrofit** for external travel-related APIs.  
- Implement **Google Maps** for trip visualization.  
- Upgrade to **Navigation 3** for unified and type-safe navigation.  
- Add **unit, integration, and UI tests**.  
- Migrate from **Firebase Auth** to **Credential Manager**.

---

## 📱 Status
🚧 **Currently in active development** — Polishing UI state management, and actively migrating to **Navigation 3** for type-safe routing.
