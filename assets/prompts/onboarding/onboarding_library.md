Aider Architektur-Prompt: :core:onboarding Library (Jetpack Compose)
Erstelle ein neues Submodul :core:onboarding für das Android-Projekt im Namespace com.scto.mcs. Dieses Modul stellt eine moderne, reine Jetpack Compose-Alternative zur Bibliothek "AppIntro" (https://github.com/AppIntro/AppIntro) dar. Es dürfen keine Fragments oder XML-Layouts verwendet werden, ausschließlich Kotlin und Compose!
1. Projekt-Kontext, Modul-Struktur & Gradle-Setup
Nutze den beiliegenden Projekt-Tree (MCS_20260502_125638.md) als Referenz. Lege das neue Modul unter core/onboarding/ an.
Führe zwingend folgende Build-Schritte nacheinander aus:
* Schritt A - Modul registrieren: Füge include(":core:onboarding") in die Root settings.gradle.kts Datei ein.
* Schritt B - Version Catalog aktualisieren: Öffne die Datei gradle/libs.versions.toml. Prüfe, ob die folgenden Abhängigkeiten existieren, und trage sie mitsamt aktueller Versionen in die Blöcke [versions] und [libraries] ein, falls sie fehlen:
   * Compose Foundation (für den Pager: androidx.compose.foundation:foundation)
   * Coil für Compose (für das Laden von imageUri: z.B. io.coil-kt:coil-compose)
   * Material 3 (für UI Komponenten)
   * Hilt (falls in diesem Modul benötigt)
* Schritt C - Build-Script erstellen: Erstelle core/onboarding/build.gradle.kts. Verwende hierbei ausschließlich die Alias-Referenzen aus der soeben aktualisierten libs.versions.toml (z.B. implementation(libs.coil.compose)). Trage keine hardcodierten Versions-Strings ein!
* Schritt D - Verzeichnisstruktur: Lege die Ordnerstruktur core/onboarding/src/main/java/com/scto/mcs/core/onboarding/ an.
2. Datenmodelle & State (MVVM Architektur)
Erstelle im Paket com.scto.mcs.core.onboarding.state:
* OnboardingPage (Data Class): Repräsentiert eine einzelne Slide. Eigenschaften:
   * title (String / StringRes)
   * description (String / StringRes)
   * imageRes (Int / DrawableRes) oder imageUri (für Lottie/Coil)
   * backgroundColor (Compose Color)
* OnboardingUiState (Data Class): Der globale State für den Screen. Eigenschaften:
   * pages (List<OnboardingPage>)
   * isCompleted (Boolean - true, wenn der Nutzer auf "Done" oder "Skip" geklickt hat)
* OnboardingViewModel (HiltViewModel):
   * Hält einen StateFlow<OnboardingUiState>.
   * Bietet Intent-Funktionen an: onSkipClicked(), onNextClicked(), onDoneClicked().
3. UI-Komponenten (Jetpack Compose)
Erstelle im Paket com.scto.mcs.core.onboarding.ui:
* OnboardingScreen (Haupt-Composable):
   * Nutzt einen HorizontalPager (aus androidx.compose.foundation.pager), um durch die pages des OnboardingUiState zu swipen.
   * Implementiert einen sanften Hintergrundfarben-Übergang (Color Animation via animateColorAsState) passend zur aktuellen Slide, ähnlich wie bei AppIntro.
   * Nimmt Callbacks entgegen (z.B. onOnboardingFinished: () -> Unit), um die Navigation im übergeordneten App-Graph zu triggern.
* OnboardingPageContent (Composable):
   * Das Design für eine einzelne Seite. Zeigt zentriert das Bild (via Image oder Coils AsyncImage), darunter den Titel (groß/bold) und die Beschreibung.
* OnboardingBottomBar (Composable):
   * Die untere Steuerleiste, bestehend aus:
      * Links: "Skip"-Button (versteckt sich ggf. auf der letzten Seite).
      * Mitte: Ein moderner Pager Indicator (Punkte, die die aktuelle Seite anzeigen).
      * Rechts: "Next"-Button (bzw. "Done"-Button auf der allerletzten Seite).
4. Implementierungs-Details
* Behalte den Clean-Architecture Ansatz bei und bevorzuge sauberen, idiomatischen Kotlin-Code.
* Verwende Material 3 Design-Guidelines für die Typografie und Buttons.
* Stelle sicher, dass die OnboardingScreen API so gestaltet ist, dass ein Feature-Modul sie einfach mit einer Liste von OnboardingPage-Objekten füttern kann.
Bitte setze diese Anforderungen schrittweise in Code um.