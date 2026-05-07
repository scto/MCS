# MCS Projekt-Refactoring & Ressourcen-Zentralisierungs Plan (Final)
Dieses Dokument dient als Master-Plan für die Konsolidierung des Projekts auf den Namespace com.scto.mcs. Dieser Plan nutzt die Datei MCS_20260506_143519.md (Project Tree), um alle Pfade, Module und Import-Korrekturen dynamisch zu ermitteln, teilt aber hochkomplexe Modul-Zusammenführungen (wie die XED-Migration in feature:settings und core:editor) in sichere, granulare Teilschritte auf.
## Projekt-Kontext
 * **Ziel-Package:** com.scto.mcs
 * **Struktur-Referenz:** Nutze zwingend das Attachment MCS_20260506_143519.md (Project Tree), um Pfade und Module (:core:* und :feature:*) dynamisch aufzulösen.
 * **Zu ersetzende Namespaces:** com.rk, com.srvhive, com.scto.msc
## Globale Import-Mapping Anweisung
**WICHTIG für alle Imports:** Anstatt einer fest einprogrammierten Tabelle sollst du bei *jedem Refactoring-Schritt* zwingend die Struktur aus der angehängten Datei MCS_20260506_143519.md (Project Tree) ableiten.
Analysiere den dortigen Verzeichnisbaum, um herauszufinden, in welchem Modul sich eine Klasse nun befindet, und ersetze alle veralteten com.rk.* oder com.srvhive.* Referenzen dynamisch durch den neuen, korrekten com.scto.mcs.* Pfad (z. B. alte com.rk.lsp.* Imports werden durch den korrekten Pfad im :core:editor Modul ersetzt).
## Schritt 1: Zentrale Build-Konfiguration & Version Catalog
**Aider-Aufruf:** /add build.gradle.kts settings.gradle.kts gradle/libs.versions.toml
 1. Überprüfe libs.versions.toml auf Vollständigkeit (Hilt, KSP, Compose).
 2. Stelle sicher, dass settings.gradle.kts alle Module (:core:* und :feature:*) korrekt inkludiert.
 3. Bereinige harte Versions-Strings in allen Gradle-Dateien.
## Schritt 2: Audit der Core-Module (Build-Logik)
**Aider-Aufruf:** /add MCS_20260506_143519.md core/**/build.gradle.kts
 1. Nutze den Project Tree, um alle Core-Module zu finden.
 2. Setze in jedem Modul unter :core den Namespace auf com.scto.mcs.core.<modulname>.
 3. Konfiguriere Hilt und KSP einheitlich.
## Schritt 3: Audit der Feature-Module (Build-Logik)
**Aider-Aufruf:** /add MCS_20260506_143519.md feature/**/build.gradle.kts
 1. Nutze den Project Tree, um alle Feature-Module zu finden.
 2. Setze in jedem Modul unter :feature den Namespace auf com.scto.mcs.feature.<modulname>.
 3. Verknüpfe die notwendigen :core-Module korrekt via implementation(project(":core:<name>")).
## Schritt 4: Quellcode-Refactoring (Feature-Module Allgemein)
**Aider-Aufruf:** /add MCS_20260506_143519.md feature/**/*.kt feature/**/src/main/AndroidManifest.xml
**Anweisungen:**
 1. **Dynamische Analyse:** Nutze den Project Tree, um alle Feature-Module (außer feature:settings) dynamisch zu durchlaufen. Migriere sie auf com.scto.mcs.feature.<modulname>.
 2. **WICHTIG:** Ignoriere bei diesem Schritt das Verzeichnis feature/settings/xed komplett! Das Refactoring hierfür erfolgt separat in Schritt 5.
## Schritt 5: Granulare Zusammenführung von Feature Settings (XED Migration)
Führe die folgenden Aider-Aufrufe **nacheinander** aus, um Kontext-Abbrüche zu vermeiden. Nutze für jeden Schritt die **Project Tree Datei (MCS_20260506_143519.md)** zur Ableitung der neuen Import-Pfade.
### 5.1 Migration: about
**Aider-Aufruf:** /add MCS_20260506_143519.md feature/settings/src/main/java/com/scto/mcs/feature/settings/xed/about/**/*.kt
 1. Verschiebe alle Dateien von .../xed/about/ nach .../about/ (eine Ebene nach oben).
 2. Setze das Package auf com.scto.mcs.feature.settings.about.
 3. Korrigiere interne Imports basierend auf der Tree-Datei.
### 5.2 Migration: app
**Aider-Aufruf:** /add MCS_20260506_143519.md feature/settings/src/main/java/com/scto/mcs/feature/settings/xed/app/**/*.kt
 1. Verschiebe alle Dateien von .../xed/app/ nach .../app/.
 2. Setze das Package auf com.scto.mcs.feature.settings.app.
 3. Korrigiere interne Imports basierend auf der Tree-Datei.
### 5.3 Migration: editor
**Aider-Aufruf:** /add MCS_20260506_143519.md feature/settings/src/main/java/com/scto/mcs/feature/settings/xed/editor/**/*.kt
 1. Verschiebe alle Dateien von .../xed/editor/ nach .../editor/.
 2. Setze das Package auf com.scto.mcs.feature.settings.editor.
 3. Korrigiere interne Imports basierend auf der Tree-Datei.
### 5.4 Migration: terminal
**Aider-Aufruf:** /add MCS_20260506_143519.md feature/settings/src/main/java/com/scto/mcs/feature/settings/xed/terminal/**/*.kt
 1. Verschiebe alle Dateien von .../xed/terminal/ nach .../terminal/.
 2. Setze das Package auf com.scto.mcs.feature.settings.terminal.
 3. Korrigiere interne Imports basierend auf der Tree-Datei.
### 5.5 Migration: git, theme, extension
**Aider-Aufruf:** /add MCS_20260506_143519.md feature/settings/src/main/java/com/scto/mcs/feature/settings/xed/git/**/*.kt feature/settings/src/main/java/com/scto/mcs/feature/settings/xed/theme/**/*.kt feature/settings/src/main/java/com/scto/mcs/feature/settings/xed/extension/**/*.kt
 1. Verschiebe die Dateien aus den xed-Unterordnern eine Ebene nach oben in ihre jeweiligen Module.
 2. Setze das Package auf com.scto.mcs.feature.settings.<ordner>.
 3. Korrigiere interne Imports basierend auf der Tree-Datei.
### 5.6 Migration: Restliche Sub-Ordner
**Aider-Aufruf:** /add MCS_20260506_143519.md feature/settings/src/main/java/com/scto/mcs/feature/settings/xed/keybinds/**/*.kt feature/settings/src/main/java/com/scto/mcs/feature/settings/xed/language/**/*.kt feature/settings/src/main/java/com/scto/mcs/feature/settings/xed/lsp/**/*.kt feature/settings/src/main/java/com/scto/mcs/feature/settings/xed/runners/**/*.kt feature/settings/src/main/java/com/scto/mcs/feature/settings/xed/support/**/*.kt feature/settings/src/main/java/com/scto/mcs/feature/settings/xed/debugOptions/**/*.kt
 1. Verschiebe auch diese restlichen Ordner eine Ebene nach oben.
 2. Aktualisiere Packages und Imports basierend auf der Tree-Datei.
### 5.7 Migration: Root-Dateien in XED
**Aider-Aufruf:** /add MCS_20260506_143519.md feature/settings/src/main/java/com/scto/mcs/feature/settings/xed/*.kt
 1. Verschiebe alle noch direkt im xed-Ordner verbliebenen Dateien nach feature/settings/src/main/java/com/scto/mcs/feature/settings/.
 2. Setze das Package auf com.scto.mcs.feature.settings.
### 5.8 Globale Import-Korrektur (Feature Settings)
**Aider-Aufruf:** /add MCS_20260506_143519.md feature/settings/**/*.kt app/src/main/java/com/scto/mcs/app/**/*.kt
 1. Suche in allen geladenen Dateien nach verbliebenen Imports mit dem Präfix .xed..
 2. Entferne das .xed aus diesen Pfaden.
 3. Lösche redundante/doppelte Imports.
### 5.9 XED Cleanup (Settings)
**Aider-Aufruf:** /run rm -rf feature/settings/src/main/java/com/scto/mcs/feature/settings/xed
 1. Lösche das nun leere xed Verzeichnis final aus dem Dateibaum.
## Schritt 6: Quellcode-Refactoring (Core-Submodule Allgemein)
**Aider-Aufruf:** /add MCS_20260506_143519.md core/**/*.kt
**Anweisungen:**
 1. **Dynamische Ermittlung:** Nutze den Project Tree (MCS_20260506_143519.md), um alle Core-Submodule dynamisch zu identifizieren.
 2. **Migration:** Führe für jedes identifizierte Core-Modul eine Migration auf com.scto.mcs.core.<modulname> durch.
 3. **Import-Korrektur:** Leite die neuen Pfade zwingend aus dem angehängten Project Tree ab.
 4. **WICHTIG:** Ignoriere bei diesem Schritt das Verzeichnis core/editor/src/main/java/com/scto/mcs/core/editor/xed komplett! Dies wird im nächsten Schritt granular erledigt.
## Schritt 7: Granulare Zusammenführung von Core Editor (XED Migration)
Genauso wie bei den Settings, muss der xed Ordner im :core:editor Modul sicher und in Teilschritten aufgelöst werden. Führe nach jedem Unterpunkt /clear und /drop aus!
### 7.1 Migration: LSP
**Aider-Aufruf:** /add MCS_20260506_143519.md core/editor/src/main/java/com/scto/mcs/core/editor/xed/lsp/**/*.kt
 1. Verschiebe alle Dateien von .../xed/lsp/ nach .../lsp/ (eine Ebene nach oben).
 2. Setze das Package auf com.scto.mcs.core.editor.lsp.
 3. Korrigiere interne Imports basierend auf der Tree-Datei.
### 7.2 Migration: Search
**Aider-Aufruf:** /add MCS_20260506_143519.md core/editor/src/main/java/com/scto/mcs/core/editor/xed/search/**/*.kt
 1. Verschiebe alle Dateien von .../xed/search/ nach .../search/.
 2. Setze das Package auf com.scto.mcs.core.editor.search.
 3. Korrigiere interne Imports.
### 7.3 Migration: Tabs
**Aider-Aufruf:** /add MCS_20260506_143519.md core/editor/src/main/java/com/scto/mcs/core/editor/xed/tabs/**/*.kt
 1. Verschiebe alle Dateien von .../xed/tabs/ nach .../tabs/.
 2. Setze das Package auf com.scto.mcs.core.editor.tabs.
 3. Korrigiere interne Imports.
### 7.4 Migration: Restliche Sub-Ordner
**Aider-Aufruf:** /add MCS_20260506_143519.md core/editor/src/main/java/com/scto/mcs/core/editor/xed/*/**/*.kt *(ohne lsp, search, tabs)*
 1. Verschiebe alle verbleibenden Unterordner (z.B. formatters, api, util) eine Ebene nach oben.
 2. Aktualisiere Packages auf com.scto.mcs.core.editor.<ordner>.
 3. Korrigiere interne Imports.
### 7.5 Migration: Root-Dateien in Editor XED
**Aider-Aufruf:** /add MCS_20260506_143519.md core/editor/src/main/java/com/scto/mcs/core/editor/xed/*.kt
 1. Verschiebe alle noch direkt im xed-Ordner verbliebenen Dateien nach .../core/editor/.
 2. Setze das Package auf com.scto.mcs.core.editor.
### 7.6 Globale Import-Korrektur (Core Editor)
**Aider-Aufruf:** /add MCS_20260506_143519.md core/editor/**/*.kt
 1. Suche im gesamten Editor-Modul nach verbliebenen Imports mit dem Präfix .xed..
 2. Entferne das .xed aus diesen Pfaden.
### 7.7 XED Cleanup (Editor)
**Aider-Aufruf:** /run rm -rf core/editor/src/main/java/com/scto/mcs/core/editor/xed
 1. Lösche das nun leere xed Verzeichnis final aus dem Dateibaum des Editor-Moduls.
## Schritt 8: Ressourcen & Strings (Zentralisierung)
### 8.1 Ressourcen-Verschiebung (Core Submodule)
**Aider-Aufruf:** /add MCS_20260506_143519.md core/**/src/main/res/**/*.xml
**Anweisungen:**
 1. Verschiebe alle Ressourcen aus den im Tree gelisteten Core-Submodulen nach :core:resources (core/resources/src/main/res/).
### 8.2 Ressourcen-Verschiebung (Feature Submodule)
**Aider-Aufruf:** /add MCS_20260506_143519.md feature/**/src/main/res/**/*.xml
**Anweisungen:**
 1. Verschiebe alle Ressourcen aus den im Tree gelisteten Feature-Submodulen nach :core:resources (core/resources/src/main/res/).
## Schritt 9: String-Management & Code-Anpassung
### 9.1 Core Submodule
**Aider-Aufruf:** /add MCS_20260506_143519.md core/**/src/main/java/**/*.kt core/**/src/main/java/**/*.java core/resources/src/main/res/values/strings.xml
**Anweisungen:**
 1. Führe alle strings.xml in der zentralen Datei zusammen (Duplikate entfernen).
 2. Ersetze hardkodierte UI-Strings im Code durch R.string-Referenzen.
 3. Aktualisiere alle Ressourcen-Imports auf com.scto.mcs.core.resources.R.
### 9.2 Feature Submodule
**Aider-Aufruf:** /add MCS_20260506_143519.md feature/**/src/main/java/**/*.kt feature/**/src/main/java/**/*.java core/resources/src/main/res/values/strings.xml
**Anweisungen:**
 1. Modifiziere die zentrale Datei core/resources/src/main/res/values/strings.xml, indem du neue <string>-Elemente am Ende des <resources>-Blocks einfügst.
 2. Extrahiere hartkodierte UI-Strings (Ignoriere Log-Ausgaben, Keys etc.).
 3. Generiere eindeutige Keys (feature_<modulname>_<deskriptiver_name>).
 4. Ersetze die Strings im Code und nutze immer com.scto.mcs.core.resources.R.
## Schritt 10: App-Modul & Finale Integration
**Aider-Aufruf:** /add MCS_20260506_143519.md app/build.gradle.kts app/src/main/AndroidManifest.xml
 1. Finalisiere :app auf com.scto.mcs.app.
 2. Validiere Manifest-Pfade (Activities, Services) und Permissions.
 3. Behebe letzte Kompilierfehler durch Namespace-Mismatch.
