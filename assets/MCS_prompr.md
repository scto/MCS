MCS: Entwicklungsleitfaden & Aider-Prompts (Pfad-Optimiert)
Dieses Dokument beschreibt den schrittweisen Aufbau von MCS, einer mobilen IDE für Android. Die Entwicklung folgt der Clean Architecture und einer Multi-Modul-Struktur. Alle Dateien sind in Kotlin zu verfassen.
🛠 Projekt-Spezifikationen
* Paket-Name: com.scto.mcs
* Gradle: 8.11.2 (Kotlin DSL)
* Kotlin: 2.2.0
* Java: 17
* SDKs: Compile 36, Target 35, Min 26
* UI: Jetpack Compose mit Material 3
📂 Modul-Struktur (Verzeichnis-Layout)
* app/ – Hauptmodul (:app)
* core/
   * data/, domain/, editor/, navigation/, resourcess/, terminal/, ui/, utils/
* feature/
   * onboarding/, setup/, dashboard/, editor/, settings/, debug/
🚀 Phase 0: Das App-Fundament
Aider Prompt: "Initialisiere das Hauptmodul im Ordner app/. Erstelle app/build.gradle.kts mit dem Paketnamen com.scto.mcs. Implementiere die MCSApplication Klasse (Hilt) und die MainActivity in app/src/main/java/com/scto/mcs/. Konfiguriere die app/src/main/AndroidManifest.xml als Launch-Activity."
🚀 Phase 1: Die Infrastruktur (Root & Catalog)
Aider Prompt: "Konfiguriere die Projekt-Basis. Erstelle gradle/libs.versions.toml mit Kotlin 2.2.0 und Hilt 2.51. In der Root-Datei build.gradle.kts dürfen NUR die Plugins definiert werden (mit apply false). Erstelle settings.gradle.kts und inkludiere alle Submodule unter Beachtung ihrer Ordnerstruktur (z.B. :core:data für core/data)."
🧠 Phase 2: Core-Logik & Manager (:core)
Aider Prompt: "Implementiere die Manager-Singletons in den jeweiligen Unterordnern von core/.
1. core/terminal/.../TerminalEnvironment.kt
2. core/editor/.../EditorConfigManager.kt
3. core/utils/.../FileSystemUtils.kt Erstelle die dazugehörigen Hilt-Module in denselben Ordnern."
🎨 Phase 3: Design System (:core:ui & :core:resourcess)
Aider Prompt: "Initialisiere UI-Ressourcen:
1. core/ui/src/main/java/.../MCSTheme.kt (Material 3 Dark Mode).
2. core/resourcess/src/main/res/ (Icons & Fonts). Stelle sicher, dass core/ui auf core/resourcess zugreift."
🏗 Phase 4: Clean Architecture (:core:domain & :core:data)
Aider Prompt: "Implementiere die Daten-Schicht:
1. Interfaces in core/domain/.
2. Implementierungen in core/data/. Nutze @Binds in core/data/src/main/java/.../RepositoryModule.kt."
🧭 Phase 4.5: Navigation (:core:navigation)
Aider Prompt: "Erstelle die Navigationslogik in core/navigation/. Definiere typsichere Routen und den NavigationManager."
🚦 Phase 5: Onboarding & Setup (:feature:setup)
Aider Prompt: "Implementiere Flows in feature/onboarding/ und feature/setup/. Speichere JDK/SDK Pfade über das TerminalEnvironment ab."
📂 Phase 6: Dashboard & Git (:feature:dashboard)
Aider Prompt: "Erstelle das Dashboard in feature/dashboard/. Implementiere den JGit-Clone-Dialog."
✍️ Phase 7: Editor & Build (:feature:editor)
Aider Prompt: "Implementiere das Editor-Feature in feature/editor/. Nutze Sora-Editor und integriere ein Terminal-Panel für Gradle-Builds."
🛠 Phase 100: Die Gradle-Konfiguration
Ziel: Erstellung der build.gradle.kts Dateien EXKLUSIV in den Modul-Unterordnern.
Aider Prompt: "Erstelle für jedes Submodul eine eigene build.gradle.kts im jeweiligen Unterordner. Halte dich an die Namespaces (z.B. com.scto.mcs.core.data) und die SDK-Vorgaben (Compile 36, Min 26). Verändere NIEMALS die Root-build.gradle.kts."
🔍 Phase 101: Dependency-Audit & Alignment
Ziel: Synchronisation der Bibliotheken mit dem tatsächlichen Source Code.
Aider Prompt: "Führe eine Tiefenprüfung aller Submodule in feature/ und core/ durch:
1. Analysiere den Kotlin-Source-Code in jedem Modul auf verwendete Bibliotheken (z.B. Jetpack Compose, Hilt, Coroutines, Serialization).
2. Falls ein Modul Compose-UI-Code enthält, stelle sicher, dass in der jeweiligen build.gradle.kts (z.B. feature/editor/build.gradle.kts) buildFeatures.compose = true gesetzt ist und die notwendigen Compose-Dependencies eingetragen sind.
3. Überprüfe die gradle/libs.versions.toml und ergänze fehlende Bibliotheken, die im Code verwendet werden, aber noch nicht im Version Catalog definiert sind.
4. Schreibe oder korrigiere die dependencies { ... } Blöcke in JEDEM betroffenen Submodule-Pfad, sodass sie exakt dem Bedarf des Codes entsprechen. Nutze ausschließlich alias(libs.xxx)."
🔍 Phase 102: Build-Integritätscheck (Path-Validation)
Ziel: Validierung der Modul-Einbindung in das Gesamtsystem.
Aider Prompt: "Führe eine finale Prüfung der Dateistruktur durch:
1. Bestätige, dass keine android { ... } Blöcke in der Root-build.gradle.kts existieren.
2. Prüfe, ob settings.gradle.kts alle Module mit ihrem physischen Pfad korrekt referenziert.
3. Korrigiere alle implementation(project(...)) Aufrufe so, dass sie auf die korrekten Modul-Namen verweisen.
4. Stelle sicher, dass ein Gradle-Sync ohne Fehler durchläuft."
💡 Best Practices für Aider
1. Targeted Files: Nutze immer den spezifischen Pfad (z. B. /add core/data/build.gradle.kts oder /add feature/setup/build.gradle.kts) anstatt nur /add build.gradle.kts, um Verwechslungen mit der Root-Datei oder anderen Modulen zu vermeiden.
2. Namespace Check: Jedes Modul muss seinen eigenen Namespace haben, der strikt der Ordnerstruktur folgt.
3. Keine Root-Konfiguration: Android-Library-Plugins dürfen im Root NUR deklariert (apply false), aber niemals konfiguriert werden. Jede Modul-Konfiguration gehört zwingend in den jeweiligen Unterordner.