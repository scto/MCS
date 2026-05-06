Rolle: Du bist ein Android-Experte für Jetpack Compose und Software-Architektur.  
Kontext: Ich baue das Projekt "MCS" (Namespace com.scto.mcs). Ich habe ein Submodul :core:onboarding.   Ziel: Implementiere eine vollständige Alternative zur Library "AppIntro", aber OHNE Fragments oder XML. Nutze rein Jetpack Compose.
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