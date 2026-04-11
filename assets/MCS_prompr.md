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
🛠 Phase 100: Die "Peinlich Genaue" Gradle-Konfiguration
Ziel: Erstellung der build.gradle.kts Dateien EXKLUSIV in den Modul-Unterordnern.
Aider Prompt: "Erstelle für jedes Submodul eine eigene build.gradle.kts im jeweiligen Unterordner.
STRIKTE REGELN:
1. Verändere NIEMALS die Root-build.gradle.kts für Modul-Konfigurationen.
2. Jede Datei muss plugins { alias(libs.plugins.android.library); alias(libs.plugins.kotlin.android); ... } enthalten.
3. Der namespace muss exakt zum Pfad passen.
Zu erstellende Dateien (Inhalt peinlich genau prüfen):
* core/data/build.gradle.kts (Namespace: com.scto.mcs.core.data)
* core/domain/build.gradle.kts (Namespace: com.scto.mcs.core.domain)
* core/editor/build.gradle.kts (Namespace: com.scto.mcs.core.editor)
* core/navigation/build.gradle.kts (Namespace: com.scto.mcs.core.navigation)
* core/resourcess/build.gradle.kts (Namespace: com.scto.mcs.core.resourcess)
* core/terminal/build.gradle.kts (Namespace: com.scto.mcs.core.terminal)
* core/ui/build.gradle.kts (Namespace: com.scto.mcs.core.ui)
* core/utils/build.gradle.kts (Namespace: com.scto.mcs.core.utils)
* feature/onboarding/build.gradle.kts (Namespace: com.scto.mcs.feature.onboarding)
* feature/setup/build.gradle.kts (Namespace: com.scto.mcs.feature.setup)
* feature/dashboard/build.gradle.kts (Namespace: com.scto.mcs.feature.dashboard)
* feature/editor/build.gradle.kts (Namespace: com.scto.mcs.feature.editor)
* feature/settings/build.gradle.kts (Namespace: com.scto.mcs.feature.settings)
* feature/debug/build.gradle.kts (Namespace: com.scto.mcs.feature.debug)
Alle Module nutzen compileSdk = 36 und minSdk = 26 aus dem Version Catalog."
🔍 Phase 101: Build-Integritätscheck (Path-Validation)
Aider Prompt: "Führe eine finale Prüfung der Dateistruktur durch:
1. Bestätige, dass keine android { ... } Blöcke in der Root-build.gradle.kts existieren. Falls doch, verschiebe sie in die korrekten Modul-Unterordner und lösche sie im Root.
2. Prüfe, ob settings.gradle.kts alle Module mit ihrem physischen Pfad referenziert (z.B. include(":core:data"); project(":core:data").projectDir = file("core/data")).
3. Korrigiere alle implementation(project(...)) Aufrufe so, dass sie auf die korrekten Modul-Namen verweisen."
💡 Best Practices für Aider
1. Targeted Files: Nutze immer den spezifischen Pfad (z. B. /add core/data/build.gradle.kts oder /add feature/setup/build.gradle.kts) anstatt nur /add build.gradle.kts, um Verwechslungen mit der Root-Datei oder anderen Modulen zu vermeiden.
2. Namespace Check: Jedes Modul muss seinen eigenen Namespace haben, der strikt der Ordnerstruktur folgt.
3. Keine Root-Konfiguration: Android-Library-Plugins dürfen im Root NUR deklariert (apply false), aber niemals konfiguriert werden. Jede Modul-Konfiguration gehört zwingend in den jeweiligen Unterordner.