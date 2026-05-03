Prompts für die Onboarding-Entwicklung
1. Gemini Prompt (Architektur & Feature-Deep-Dive)
Dieser Prompt ist darauf optimiert, dass Gemini das logische Mapping von AppIntro (Fragment-basiert) auf Compose versteht.
Rolle: Du bist ein Android-Experte für Jetpack Compose und Software-Architektur. Kontext: Ich baue das Projekt "MCS" (Namespace com.scto.mcs). Ich habe ein Submodul :core:onboarding. Ziel: Implementiere eine vollständige Alternative zur Library "AppIntro", aber OHNE Fragments oder XML. Nutze rein Jetpack Compose.
Anforderungen basierend auf AppIntro Features:
1. Zustandsverwaltung: Erstelle einen OnboardingViewModel im Paket com.scto.mcs.core.onboarding.state, der eine Liste von OnboardingPage Objekten verwaltet.
2. Dynamik: Implementiere Funktionen addSlide, removeSlide und askForPermissions(permission: String).
3. UI Engine: Nutze HorizontalPager. Implementiere ein "Color Morphing" System, bei dem die Hintergrundfarbe sanft zwischen den Slides interpoliert wird (lerp von Farben während des Scrollens).
4. Interaktion: - Implementiere haptisches Feedback (Vibration) beim Erreichen des Endes oder beim Drücken von "Done".
   * Unterstützung für Skip, Next und Done Buttons mit Material 3 Styling.
5. Indikatoren: Baue ein System für verschiedene Indikator-Typen (Dots, Progress Bar, Icons).
6. Imports: Achte auf die Struktur des MCS-Projekts. Nutze Hilt für Dependency Injection und Coil für das Laden von Bildern/Vektoren.
Bitte generiere:
* Den idiomatischen Kotlin-Code für den OnboardingScreen (Haupt-UI).
* Die Logik für den PagerState gesteuerten Hintergrundfarben-Wechsel.
* Ein Beispiel, wie ein Feature-Modul eine "Permission-Slide" hinzufügen kann, die den System-Dialog triggert.
2. Aider Prompt (File-basiertes Refactoring)
Dieser Prompt ist kürzer und präziser für die Arbeit direkt im Terminal/Editor mit Aider.
Act as an Android Architect. I want to fully replicate the functionality of the "AppIntro" library within the module :core:onboarding (namespace com.scto.mcs) using only Jetpack Compose.
Current Context:
* Project: MCS
* Language: Kotlin
* UI: Jetpack Compose + Material 3
* Dependencies: Hilt, Coil, Pager Foundation (configured in gradle/libs.versions.toml)
Tasks:
1. Create core/onboarding/src/main/java/com/scto/mcs/core/onboarding/state/OnboardingViewModel.kt:
   * Implement thread-safe dynamic slide management (MutableStateFlow).
   * Add a vibrate() helper using VibratorManager.
2. Create core/onboarding/src/main/java/com/scto/mcs/core/onboarding/ui/OnboardingScreen.kt:
   * Use HorizontalPager from androidx.compose.foundation.pager.
   * Implement animateColorAsState for background transitions.
   * Add a custom OnboardingBottomBar with animated dot indicators.
3. Add support for "Permission Slides":
   * The slide should only allow "Next" after the specified Android permission is granted (use rememberLauncherForActivityResult).
4. Ensure all code follows Clean Architecture and uses the com.scto.mcs hierarchy correctly.