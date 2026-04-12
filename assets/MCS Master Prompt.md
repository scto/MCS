MCS: Entwicklungsleitfaden & Aider-Prompts (Version 2.7)
Dieses Dokument beschreibt den schrittweisen Aufbau von MCS, einer mobilen IDE für Android. Die Entwicklung folgt der Clean Architecture und einer Multi-Modul-Struktur. Da für das Projekt Kotlin bevorzugt wird, ist die gesamte Codebasis in Kotlin zu verfassen.
🛠 Projekt-Spezifikationen
* Paket-Name: com.scto.mcs
* Gradle: 8.11.2 (Kotlin DSL)
* Kotlin: 2.2.0
* Java: 17
* SDKs: Compile 36, Target 35, Min 26
* UI: Jetpack Compose mit Material 3
📂 Modul-Struktur (Verzeichnis-Layout)
* app/ – Hauptmodul (:app)
* core/ – Submodule für Infrastruktur & Logik (data, domain, editor, navigation, resourcess, terminal, ui, utils)
* feature/ – Submodule für UI-Features (onboarding, setup, dashboard, editor, settings, debug)

🚀 Phase 0: Das App-Fundament
Aider Prompt: "Initialisiere das Hauptmodul im Ordner app/. Erstelle app/build.gradle.kts mit dem Paketnamen com.scto.mcs. Implementiere die MCSApplication Klasse (Hilt) und die MainActivity in app/src/main/java/com/scto/mcs/. Konfiguriere die app/src/main/AndroidManifest.xml als Launch-Activity."

🚀 Phase 1: Die Infrastruktur (Catalog, Root & Settings)
Aider Prompt: "1. Erstelle gradle/libs.versions.toml mit AGP 8.7.2, Kotlin 2.2.0 und Hilt 2.51. 2. Konfiguriere die Root-build.gradle.kts NUR mit Plugin-Deklarationen (apply false). Keine android { } Blöcke im Root. 3. Erstelle die settings.gradle.kts mit explizitem Pfad-Mapping:
include(":app")
val coreModules = listOf("data", "domain", "editor", "navigation", "resourcess", "terminal", "ui", "utils")
coreModules.forEach { name ->
   include(":core:$name")
   project(":core:$name").projectDir = file("core/$name")
}
val featureModules = listOf("onboarding", "setup", "dashboard", "editor", "settings", "debug")
featureModules.forEach { name ->
   include(":feature:$name")
   project(":feature:$name").projectDir = file("feature/$name")
}
```"

## 🧠 Phase 2: Core-Logik & Manager (:core)
**Aider Prompt:**
"Implementiere die Manager-Singletons in den jeweiligen Unterordnern von `core/`. Erstelle die dazugehörigen Hilt-Module in denselben Ordnern (z.B. `core/terminal/src/main/java/.../TerminalModule.kt`). Fokus: `TerminalEnvironment`, `EditorConfigManager` und `FileSystemUtils`."

## 🎨 Phase 3: Design System (:core:ui & :core:resourcess)
**Ziel:** Definition eines professionellen Dark-Mode IDE Themes.

**Aider Prompt:**
"Initialisiere das Design-System im Modul `core:ui` unter Nutzung von Ressourcen aus `core:resourcess`.
1. Erstelle `core/ui/src/main/java/com/scto/mcs/core/ui/theme/Color.kt` mit dieser IDE-Palette:
- `Background`: #1E1E1E (Dark Gray)
- `Surface`: #252526
- `Primary`: #007ACC (Deep Blue)
- `Secondary`: #3C3C3C
- `OnBackground/OnSurface`: #CCCCCC (Text)
2. Erstelle `core/ui/src/main/java/com/scto/mcs/core/ui/theme/Type.kt`: Definiere `Typography` mit einem Fokus auf Monospace für `bodyMedium` (für Code/Terminal).
3. Implementiere das `MCSTheme` (Material 3) in `Theme.kt`, das diese Farben nutzt.
4. Erstelle in `core/ui/components/` Basis-Komponenten: `MCSToolbar`, `MCSButton` und `MCSIcon`. Nutze Icons (SVG/Vector) aus `core:resourcess`."

## 🏗 Phase 4: Clean Architecture & ResourceProvider
**Ziel:** Trennung von Logik und Android-Framework durch Abstraktion des Ressourcenzugriffs.

**Aider Prompt:**
"Implementiere die Daten-Schicht in `core/domain/` (Interfaces) und `core/data/` (Implementierung).
1. **ResourceProvider Pattern:** - Erstelle ein `ResourceProvider` Interface in `core:domain` zum Abrufen von Strings ohne direkte Context-Abhängigkeit.
- Implementiere `ResourceProviderImpl` in `core:resourcess`, die den `@ApplicationContext` nutzt.
- Binde beide via Hilt (@Binds) in einem `ResourceModule`.
2. **UseCases:** Erstelle `LoadFileContentUseCase` und `CloneRepositoryUseCase`. Nutze den `ResourceProvider`, um lokalisierte Fehlermeldungen aus den Ressourcen zu laden."

## 🧭 Phase 4.5: Navigation (:core:navigation)
**Aider Prompt:**
"Erstelle die Navigationslogik in `core/navigation/` basierend auf Navigation Compose. Definiere typsichere Routen."

## 🚦 Phase 5: Onboarding & Setup (:feature:setup)
**Aider Prompt:**
"Implementiere den Setup-Flow in `feature/onboarding/` und `feature/setup/`. Nutze Ressourcen ausschließlich über das zentrale Modul `:core:resourcess`."

## 📂 Phase 6: Dashboard & Git (:feature:dashboard)
**Aider Prompt:**
"Erstelle das Dashboard in `feature/dashboard/`. Integriere JGit-Funktionalitäten aus `core:domain` zum Klonen von Projekten."

## ✍️ Phase 7: Editor & Build (:feature:editor)
**Aider Prompt:**
"Implementiere das Editor-Feature in `feature/editor/`. Nutze Sora-Editor und integriere ein Terminal-Panel. Verknüpfe das Farbschema des Editors mit dem `MCSTheme`."

## 📦 Phase 8: Ressourcen-Zentralisierung & Manifest-Cleanup
**Ziel:** Radikale Konsolidierung aller Android-Assets in `:core:resourcess` und Bereinigung aller anderen Module.

**Aider Prompt:**
"Führe eine strikte Ressourcen-Zentralisierung durch:
1. **Transfer:** Verschiebe ALLE Dateien aus `res/` Ordnern aller Module (app, feature, core) nach `core/resourcess/src/main/res/`.
2. **Konflikte:** Bei Dateinamenskollisionen nutze Modul-Präfixe (z.B. `setup_icon.xml`).
3. **Strings:** Merge alle `strings.xml` Inhalte in die zentrale Datei in `:core:resourcess`. Nutze Präfixe für IDs (z.B. `editor_title`), falls IDs mehrfach vorkommen.
4. **Manifest-Regel:** Behalte die `AndroidManifest.xml` in jedem Submodul bei (erforderlich für die Modul-Definition). Entferne jedoch alle `android:label`, `android:icon` oder `android:theme` Attribute, die auf lokale Ressourcen verweisen. Falls ein Manifest Ressourcen benötigt, müssen diese explizit auf die Ressourcen in `:core:resourcess` verweisen.
5. **Kahlschlag:** Lösche restlos alle `res/` Verzeichnisse in allen Modulen AUSSER in `core/resourcess/`.
6. **Verkabelung:** Binde `:core:resourcess` in allen Modulen (inkl. `:app`) via Gradle ein und korrigiere alle `R`-Importe auf `com.scto.mcs.core.resourcess.R`."

## 🛠 Phase 100: Die Modul-Gradle-Konfiguration
**Aider Prompt:**
"Erstelle für jedes Submodul eine eigene `build.gradle.kts` im jeweiligen Unterordner. Nutze ausschließlich den Version Catalog. Stelle sicher, dass der Namespace korrekt ist (z.B. `com.scto.mcs.core.ui`)."

## 🔍 Phase 101: Dependency-Audit & Alignment
**Aider Prompt:**
"Prüfe den Source-Code in `feature/` und `core/`. 
1. Aktiviere `buildFeatures.compose = true` in jedem Modul, das Compose-Funktionen nutzt.
2. Synchronisiere die `dependencies { ... }` Blöcke in den Submodulen mit dem tatsächlichen Bedarf (Hilt, Compose, Navigation etc.).
3. Ergänze fehlende Einträge in der `libs.versions.toml` falls nötig."

## 🔍 Phase 102: Build-Integritätscheck (Path-Validation)
**Aider Prompt:**
"Führe eine finale Prüfung durch: Keine Android-Konfigurationen im Root! Bestätige das Pfad-Mapping in `settings.gradle.kts` und die Modul-Abhängigkeiten. Der Gradle-Sync muss fehlerfrei durchlaufen."

## 💡 Best Practices für Aider
1. **Targeted Files:** Nutze immer den spezifischen Pfad (z. B. `/add core/ui/src/main/java/com/scto/mcs/core/ui/theme/Theme.kt`) anstatt nur den Dateinamen, um Verwechslungen zu vermeiden.
2. **Namespace Check:** Jedes Modul muss seinen eigenen Namespace haben, der strikt der Ordnerstruktur folgt.
3. **Keine Root-Konfiguration:** Android-Konfigurationen gehören zwingend in die Unterordner. Der Root dient nur der Plugin-Deklaration.