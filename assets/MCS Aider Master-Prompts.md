MCS: Master-Liste der Aider-Prompts
Diese Datei enthält alle spezifischen Prompts für die Entwicklung der MCS Android IDE. Befolge die Phasen nacheinander und achte darauf, nach jeder Phase einen Gradle-Sync in Android Studio durchzuführen.
🚀 Phase 0 & 1: Infrastruktur & App-Fundament
Ziel: Initialisierung des Projekts, der Modul-Struktur und der Hilt-App.
Aider Prompt:
Initialisiere die Phasen 0 und 1 des MCS Projekts:

1. INFRASTRUKTUR (Root):
  - Erstelle 'gradle/libs.versions.toml' mit: AGP 8.7.2, Kotlin 2.2.0, Hilt 2.51, JGit 6.8.0.202311291450-r, Sora-Editor 0.23.0.
  - Erstelle die Root-Datei 'build.gradle.kts' NUR mit Plugin-Deklarationen (apply false) für android-application, android-library, kotlin-android, hilt-android und kotlin-compose. KEINE 'android { }' Blöcke im Root!
  - Erstelle 'settings.gradle.kts' mit explizitem Pfad-Mapping für core (data, domain, editor, navigation, resourcess, terminal, ui, utils) und feature (onboarding, setup, dashboard, editor, settings, debug) Module mittels einer Schleife, die 'projectDir' auf 'core/name' bzw. 'feature/name' setzt.

2. APP-MODUL (Phase 0):
  - Erstelle 'app/build.gradle.kts' (Namespace 'com.scto.mcs', compileSdk 36, minSdk 26).
  - Erstelle 'app/src/main/java/com/scto/mcs/MCSApplication.kt' (@HiltAndroidApp).
  - Erstelle 'app/src/main/java/com/scto/mcs/MainActivity.kt' als ComponentActivity mit Basis-Material3-Theme.
  - Erstelle 'app/src/main/AndroidManifest.xml' mit MCSApplication und MainActivity als Launcher.

🧠 Phase 2: Core-Logik & Manager
Ziel: Implementierung der Singleton-Manager.
Aider Prompt:
Implementiere die Core-Manager als Hilt-Singletons (@Singleton). Nutze für Context @ApplicationContext.

1. TerminalEnvironment (core/terminal): Erstelle 'TerminalEnvironment.kt'. Initialisiere Ordner (home, usr/bin, tmp) in 'filesDir'. Verwalte PATH, JAVA_HOME und ANDROID_HOME. Erstelle 'TerminalModule.kt' (Hilt).
2. EditorConfigManager (core/editor): Erstelle 'EditorConfigManager.kt'. Methoden zum Laden von TextMate-Grammatiken und Synchronisation des Editor-Farbschemas mit Material 3. Erstelle 'EditorModule.kt'.
3. FileSystemUtils (core/utils): Erstelle 'FileSystemUtils.kt'. Helper für I/O im 'mcs' Projekt-Pfad und FileProvider Setup für Datei-Sharing. Erstelle 'UtilsModule.kt'.

🎨 Phase 3: Design System (UI & Resources)
Ziel: Deep Blue IDE Look-and-Feel.
Aider Prompt:
Initialisiere das Design-System in 'core/ui' und 'core/resourcess':
1. Color.kt: Definiere Palette (Background: #1E1E1E, Surface: #252526, Primary: #007ACC, Text: #CCCCCC).
2. Type.kt: Setze Monospace-Font für Code-Anzeigen (bodyMedium).
3. Theme.kt: Erstelle 'MCSTheme' (Material 3 Dark Mode).
4. Komponenten: Erstelle 'MCSToolbar', 'MCSButton' und 'MCSIcon' in 'core/ui/components/'. Nutze SVG-Icons aus 'core/resourcess/src/main/res/drawable/'.

🏗 Phase 4: Clean Architecture (Domain & Data)
Ziel: Repositories und UseCases.
Aider Prompt:
Fülle 'core/domain' und 'core/data':
1. Domain: Erstelle Interfaces für 'ProjectRepository', 'GitRepository' und 'EditorRepository'. Erstelle UseCases: 'LoadFileContentUseCase' und 'CloneRepositoryUseCase'.
2. Data: Implementiere die Repositories. Nutze 'FileSystemUtils' und 'JGit'.
3. DI: Erstelle 'RepositoryModule.kt' in 'core/data' und nutze @Binds für die Verknüpfung.

🧭 Phase 4.5: Navigation
Ziel: Zentrales Routing.
Aider Prompt:
Implementiere 'core/navigation':
1. Erstelle 'NavigationManager.kt' als Hilt-Singleton.
2. Definiere typsichere Kotlin-Routen für Onboarding, Setup, Dashboard, Editor und Settings.
3. Nutze Jetpack Navigation Compose für das Hosting in der MainActivity.

🚦 Phase 5, 6 & 7: Feature-Implementierung
Ziel: Die funktionalen Screens.
Aider Prompt (Phase 5 - Setup):
Implementiere 'feature/onboarding' (Permission Handling für MANAGE_EXTERNAL_STORAGE) und 'feature/setup' (Dialoge für JDK/SDK Wahl). Zeige Installations-Fortschritt in einer Terminal-View an.

Aider Prompt (Phase 6 - Dashboard):
Implementiere 'feature/dashboard': Projekt-Liste, "Neu"-Button und "Clone"-Option (via JGit). Navigiere bei Erfolg zum Editor.

Aider Prompt (Phase 7 - Editor):
Implementiere 'feature/editor': Integriere den Sora-Editor via AndroidView. Binde Syntax-Highlighting über 'EditorConfigManager' ein. Implementiere ein Terminal-Panel (unten), das Gradle-Builds ('./gradlew assembleDebug') ausführt und den Output streamt.

🛠 Phasen 100-102: Finalisierung & Audit
Ziel: Korrekte Gradle-Struktur und Dependency-Integrität.
Aider Prompt (Phase 100):
Erstelle für JEDES Submodul in 'core/' und 'feature/' eine eigene 'build.gradle.kts' im jeweiligen Unterordner. Jedes Modul muss seinen eigenen Namespace haben (z.B. 'com.scto.mcs.core.data'). Nutze 'compileSdk = 36'. Ändere NICHTS in der Root-Datei!

Aider Prompt (Phase 101):
Audit-Phase: Analysiere den Kotlin-Code aller Submodule. Aktiviere 'buildFeatures.compose = true' NUR in Modulen mit UI-Code. Synchronisiere die 'dependencies { ... }' Blöcke mit dem tatsächlichen Bedarf (Hilt, Compose, Navigation etc.) unter Nutzung der 'libs.versions.toml'.

Aider Prompt (Phase 102):
Integritätscheck: Stelle sicher, dass keine Android-Blöcke im Root existieren. Verifiziere das Modul-Mapping in 'settings.gradle.kts'. Korrigiere fehlerhafte 'implementation(project(...))' Aufrufe. Führe einen finalen Gradle-Sync durch.

💡 Best Practices für Aider
1. Targeted Files: Nutze immer den vollen Pfad (z.B. /add core/data/build.gradle.kts), niemals nur den Dateinamen.
2. Context: Gib Aider immer die relevanten Dateien mit /add vor dem Prompt.
3. Undo: Wenn Aider den Root verschmutzt, nutze /undo und wiederhole den Befehl mit dem Hinweis "Nur in Unterordner schreiben".