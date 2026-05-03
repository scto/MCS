Aider Architektur-Prompt: :core:onboarding Library (Jetpack Compose)
Erstelle ein neues Submodul :core:onboarding für das Android-Projekt im Namespace com.scto.mcs. Dieses Modul stellt eine moderne, reine Jetpack Compose-Alternative zur Bibliothek "AppIntro" (https://github.com/AppIntro/AppIntro) dar. Es dürfen keine Fragments oder XML-Layouts verwendet werden, ausschließlich Kotlin und Compose!
1. Projekt-Kontext & Modul-Struktur
Nutze den beiliegenden Projekt-Tree (MCS_20260502_125638.md) als Referenz. Lege das neue Modul unter core/onboarding/ an. Die Struktur muss so aussehen:
* core/onboarding/build.gradle.kts (mit Compose-, Material 3- und Hilt-Dependencies)
* core/onboarding/src/main/java/com/scto/mcs/core/onboarding/
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
   * (Optional) Injiziere hier einen DataStore/Preferences-Manager, um zu speichern, dass das Onboarding abgeschlossen wurde, damit es beim nächsten App-Start nicht mehr gezeigt wird.
3. UI-Komponenten (Jetpack Compose)
Erstelle im Paket com.scto.mcs.core.onboarding.ui:
* OnboardingScreen (Haupt-Composable):
   * Nutzt einen HorizontalPager (aus androidx.compose.foundation.pager), um durch die pages des OnboardingUiState zu swipen.
   * Implementiert einen sanften Hintergrundfarben-Übergang (Color Animation) passend zur aktuellen Slide, ähnlich wie bei AppIntro.
   * Nimmt Callbacks entgegen (z.B. onOnboardingFinished: () -> Unit), um die Navigation im übergeordneten App-Graph zu triggern.
* OnboardingPageContent (Composable):
   * Das Design für eine einzelne Seite. Zeigt zentriert das Bild, darunter den Titel (groß/bold) und die Beschreibung.
* OnboardingBottomBar (Composable):
   * Die untere Steuerleiste, bestehend aus:
      * Links: "Skip"-Button (versteckt sich ggf. auf der letzten Seite).
      * Mitte: Ein moderner Pager Indicator (Punkte, die die aktuelle Seite anzeigen. Nutze eine Custom-Implementierung oder eine Standard-Compose-Lösung für Dot-Indikatoren).
      * Rechts: "Next"-Button (bzw. "Done"-Button auf der allerletzten Seite).
4. Implementierungs-Details
* Behalte den Clean-Architecture Ansatz bei.
* Sorge für weiche Animationen bei Seitenwechseln (animateColorAsState für den Hintergrund).
* Verwende Material 3 Design-Guidelines für die Typografie und Buttons.
* Stelle sicher, dass die OnboardingScreen API so gestaltet ist, dass ein Feature-Modul (z.B. die MainActivity oder ein WelcomeScreen) sie einfach mit einer Liste von OnboardingPage-Objekten füttern kann.
Bitte setze diese Anforderungen schrittweise in Code um. Beginne mit der build.gradle.kts, danach die Models/States, das ViewModel und abschließend die Compose UI.