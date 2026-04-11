Aider Initialisierung: Phase 0 & 1
Kopiere den folgenden Block und füge ihn in deine Aider-Session ein. Dieser Befehl setzt das gesamte Grundgerüst inklusive der von uns peinlich genau definierten Pfad-Struktur auf.
Der kombinierte Prompt
Initialisiere die Phasen 0 und 1 des MCS Projekts basierend auf diesen Regeln:

1. INFRASTRUKTUR (Root):
  - Erstelle 'gradle/libs.versions.toml' mit: AGP 8.7.2, Kotlin 2.2.0, Hilt 2.51, JGit 6.8.0.202311291450-r, Sora-Editor 0.23.0. Definiere Plugins für android-application, android-library, kotlin-android, hilt-android und kotlin-compose.
  - Erstelle die Root-Datei 'build.gradle.kts' NUR mit Plugin-Deklarationen (apply false). Keine 'android { }' Blöcke hier!
  - Erstelle 'settings.gradle.kts' mit explizitem Pfad-Mapping für core (data, domain, editor, navigation, resourcess, terminal, ui, utils) und feature (onboarding, setup, dashboard, editor, settings, debug) Module. Nutze eine Schleife für das Mapping der 'projectDir' auf 'core/name' bzw. 'feature/name'.

2. APP-MODUL (Phase 0):
  - Erstelle den Ordner 'app/'.
  - Erstelle 'app/build.gradle.kts' mit Namespace 'com.scto.mcs', compileSdk 36, minSdk 26 und Target 35. Füge Hilt und Compose Dependencies hinzu.
  - Erstelle 'app/src/main/java/com/scto/mcs/MCSApplication.kt' (erbt von Application, annotiert mit @HiltAndroidApp).
  - Erstelle 'app/src/main/java/com/scto/mcs/MainActivity.kt' als ComponentActivity mit einem Basis-Material3-Theme.
  - Erstelle 'app/src/main/AndroidManifest.xml' mit MCSApplication als Name und MainActivity als Launcher.

WICHTIG: Achte strikt darauf, dass keine Android-Konfigurationen in der Root-Datei landen!

Empfohlene Vorgehensweise in Aider
1. Vorbereitung: Falls du dich in einem leeren Verzeichnis befindest, stelle sicher, dass git init bereits ausgeführt wurde.
2. Ausführung: Kopiere den obigen Textblock als erste Nachricht an Aider.
3. Kontrolle: Überprüfe nach der Erstellung sofort, ob die build.gradle.kts im Root-Verzeichnis wirklich nur den plugins-Block enthält und keine android { ... } Sektionen.
4. Sync: Versuche danach einen Gradle-Sync in Android Studio.