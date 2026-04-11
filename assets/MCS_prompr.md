MCS: Entwicklungsleitfaden & Aider-Prompts
Dieses Dokument beschreibt den schrittweisen Aufbau von MCS, einer mobilen IDE für Android. Die Entwicklung folgt der Clean Architecture und einer Multi-Modul-Struktur. Da für das Projekt Kotlin bevorzugt wird, ist die gesamte Codebasis in Kotlin zu verfassen.
🛠 Projekt-Spezifikationen
* Paket-Name: com.scto.mcs
* Gradle: 8.11.2 (Kotlin DSL)
* Kotlin: 2.2.0
* Java: 17
* SDKs: Compile 36, Target 35, Min 26
* UI: Jetpack Compose mit Material 3
📂 Modul-Struktur
* :app – Einstiegspunkt & Hilt-Setup
* :core Submodule:
   * :core:data, :core:domain, :core:editor, :core:navigation, :core:resourcess, :core:terminal, :core:ui, :core:utils
* :feature Submodule:
   * :feature:onboarding, :feature:setup, :feature:dashboard, :feature:editor, :feature:settings, :feature:debug
🚀 Phase 0: Das App-Fundament
Ziel: Initialisierung des :app Moduls als zentraler Einstiegspunkt und Hilt-Initialisierung.
Aider Prompt: "Erstelle das :app Modul mit dem Paketnamen com.scto.mcs. Implementiere die MCSApplication Klasse, die von Application erbt und mit @HiltAndroidApp annotiert ist, in Kotlin. Erstelle eine MainActivity als ComponentActivity, die ein Basis-Material3-Theme lädt. Konfiguriere die AndroidManifest.xml so, dass MCSApplication als Name im <application>-Tag steht und die MainActivity als Launch-Activity fungiert. Stelle sicher, dass die build.gradle.kts von :app die notwendigen Hilt- und Compose-Dependencies enthält."
🚀 Phase 1: Die Infrastruktur
Ziel: Aufbau der Gradle-Struktur und des Version Catalogs.
Aider Prompt: "Konfiguriere die Projekt-Basis mit Kotlin als Hauptsprache. Erstelle oder aktualisiere die libs.versions.toml mit folgenden Versionen:
* Kotlin 2.2.0, Gradle 8.11.2, Java 17, Compose BOM (neueste)
* Hilt (2.51), JGit (6.8.0), Sora-Editor (0.23.0) Setze in der Root-build.gradle.kts den Hilt-Classpath und konfiguriere die settings.gradle.kts für alle Submodule. Stelle sicher, dass compileSdk 36 und targetSdk 35 voreingestellt sind."
🧠 Phase 2: Core-Logik & Manager (:core)
Ziel: Implementierung der "Gehirn"-Komponenten als Singletons.
Aider Prompt: "Implementiere in den spezifischen :core Submodulen die zentralen Manager als Hilt-Singletons in Kotlin:
1. TerminalEnvironment (in :core:terminal): Erstellt Ordnerstruktur (home, usr/bin, tmp) im internen App-Speicher und verwaltet PATH, JAVA_HOME und ANDROID_HOME.
2. EditorConfigManager (in :core:editor): Lädt TextMate-Grammatiken asynchron und synchronisiert das Editor-Farbschema mit Material 3.
3. FileSystemUtils (in :core:utils): Funktionen für Dateizugriffe im mcs Pfad inklusive FileProvider Setup.
4. Erstelle entsprechende Hilt-Module in diesen Submodulen, die diese Klassen bereitstellen."
🎨 Phase 3: Design System (:core:ui & :core:resourcess)
Ziel: Einheitliches Look-and-Feel der IDE.
Aider Prompt: "Initialisiere die Module :core:ui und :core:resourcess:
1. Erstelle in :core:ui das MCSTheme (Material 3) mit einer Dark-Mode Palette für IDEs (Hintergrund: #1E1E1E, Akzente: Deep Blue). Beziehe Ressourcen aus :core:resourcess.
2. Implementiere Basis-Komponenten: MCSToolbar, MCSButton und MCSIcons (Icons für Files, Folder, Git, Play, Terminal).
3. Erstelle eine TerminalText Komponente mit Monospace-Font."
🏗 Phase 4: Clean Architecture (:core:domain & :core:data)
Ziel: Trennung von Logik und technischer Umsetzung.
Aider Prompt: "Fülle die Module :core:domain und :core:data mit Kotlin-Code:
1. :core:domain: Erstelle Repository-Interfaces für ProjectRepository, GitRepository und EditorRepository. Erstelle UseCases wie LoadFileContentUseCase und CloneRepositoryUseCase.
2. :core:data: Implementiere diese Repositories unter Nutzung der Manager aus den anderen :core-Modulen (z.B. :core:utils für File I/O).
3. Verbinde die Schichten via @Binds in einem Hilt-RepositoryModule in :core:data."
🧭 Phase 4.5: Navigation (:core:navigation)
Ziel: Zentrales Routing zwischen den Features.
Aider Prompt: "Implementiere das Modul :core:navigation:
1. Erstelle eine zentrale Navigationslogik (z.B. basierend auf Jetpack Navigation Compose).
2. Definiere typsichere Kotlin-Routen/Destinations für Onboarding, Setup, Dashboard, Editor und Settings.
3. Stelle einen NavigationManager bereit, der app-weit via Hilt injiziert werden kann."
🚦 Phase 5: Onboarding & Setup (:feature:setup)
Ziel: Berechtigungserteilung und Laufzeit-Installation.
Aider Prompt: "Implementiere den Start-Flow:
1. :feature:onboarding: Screen für MANAGE_EXTERNAL_STORAGE Permission (Android 11+).
2. :feature:setup: Terminal-Setup-Screen mit Dialogen zur Wahl von JDK (17/21) und Android SDK (33-36). Simuliere den Installationsfortschritt in einer Terminal-View und speichere die Pfade in TerminalEnvironment in :core:terminal ab."
📂 Phase 6: Dashboard & Git (:feature:dashboard)
Ziel: Projektverwaltung und Klonen von Repositories.
Aider Prompt: "Implementiere das Dashboard:
1. Screen mit Optionen: Projekt öffnen, Erstellen, Clonen und Einstellungen.
2. Integriere CloneProjectDialog: Nutze GitRepository (JGit) aus :core:domain, um Repositories mit Fortschrittsanzeige in den mcs Ordner zu klonen. Navigiere nach Erfolg über :core:navigation zum Editor."
✍️ Phase 7: Editor & Build (:feature:editor)
Ziel: Das funktionale Herzstück der App.
Aider Prompt: "Implementiere das :feature:editor Modul:
1. Nutze AndroidView für den Sora-Editor. Binde Syntax-Highlighting über den EditorConfigManager (aus :core:editor) ein.
2. Implementiere ein Terminal-Panel am unteren Bildschirmrand.
3. Build-Funktion: Führe ./gradlew assembleDebug im Projektpfad aus, nutze JDK/SDK Pfade aus :core:terminal und streame den Output live in das Terminal-Panel."
🛠 Phase 100: Gradle-Konfiguration der Submodule
Ziel: Erstellung der build.gradle.kts für alle spezifischen Submodule unter Nutzung des Version Catalogs.
Aider Prompt: "Erstelle für alle folgenden Submodule die entsprechende build.gradle.kts Datei unter Verwendung des Version Catalogs (libs.versions.toml). Erweitere den Katalog bei Bedarf um notwendige Abhängigkeiten. Alle Module müssen als com.android.library konfiguriert werden (außer :app) und die Projekt-Spezifikationen (Kotlin 2.2.0, Compile SDK 36, Target SDK 35) einhalten.
Zu konfigurierende Submodule:
* core: :core:data, :core:domain, :core:editor, :core:navigation, :core:resourcess, :core:terminal, :core:ui, :core:utils
* feature: :feature:onboarding, :feature:setup, :feature:dashboard, :feature:editor, :feature:settings, :feature:debug
Stelle sicher, dass die Module die für ihre Aufgabe notwendigen Basis-Abhängigkeiten (z.B. Hilt, Kotlin-Ktx, Compose für UI-Module) enthalten."
🔍 Phase 101: Build-Integritätscheck & Registrierung
Ziel: Validierung der Modul-Einbindung in das Gesamtsystem.
Aider Prompt: "Überprüfe das gesamte Projekt auf Build-Fähigkeit:
1. Stelle sicher, dass ALLE oben genannten Submodule aus :core und :feature korrekt in der settings.gradle.kts eingetragen sind.
2. Überprüfe in den jeweiligen build.gradle.kts Dateien, ob die internen Modul-Abhängigkeiten (z.B. :feature:editor benötigt :core:editor) korrekt via implementation(project(...)) gesetzt sind.
3. Korrigiere fehlende oder fehlerhafte Pfadangaben in den Gradle-Skripten, bis ein fehlerfreier Gradle-Sync möglich ist."
💡 Best Practices für Aider
1. Kontext bewahren: Nutze /add [Dateipfad], bevor du eine neue Phase startest.
2. Build-Checks: Führe nach jeder Phase einen Gradle-Sync in Android Studio durch.
3. Fehlerbehebung: Wenn Aider einen Fehler macht, nutze /undo und beschreibe das Problem genauer.