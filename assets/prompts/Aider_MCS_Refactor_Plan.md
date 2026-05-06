# MCS Projekt-Refactoring & Ressourcen-Zentralisierungs Plan (Final)
Dieses Dokument dient als Master-Plan für die Konsolidierung des Projekts auf den Namespace com.scto.mcs. Die manuellen Verzeichnislisten wurden entfernt; stattdessen nutzt dieser Plan die Datei MCS_20260506_143519.md (Project Tree), um alle Pfade und Module dynamisch zu ermitteln.
## Projekt-Kontext
 * **Ziel-Package:** com.scto.mcs
 * **Struktur-Referenz:** Nutze zwingend das Attachment MCS_20260506_143519.md (Project Tree), um Pfade und Module (:core:* und :feature:*) dynamisch aufzulösen.
 * **Zu ersetzende Namespaces:** com.rk, com.srvhive, com.scto.msc
## Globale Import-Mapping Tabelle (Alt -> Neu)
Aider soll bei jedem Refactoring-Schritt diese Tabelle nutzen, um alte Referenzen zu ersetzen:
| Alter Pfad (com.rk / com.srvhive) | Neuer Pfad (com.scto.mcs) |
|---|---|
| com.rk.App | com.scto.mcs.app.App |
| com.rk.activities.main.MainActivity | com.scto.mcs.app.ui.activities.main.MainActivity |
| com.rk.activities.settings.SettingsActivity | com.scto.mcs.app.ui.activities.settings.SettingsActivity |
| com.rk.activities.terminal.Terminal | com.scto.mcs.app.ui.activities.terminal.TerminalActivity |
| com.rk.file.* | com.scto.mcs.core.files.* |
| com.rk.utils.* | com.scto.mcs.core.utils.* |
| com.rk.resources.* | com.scto.mcs.core.resources.* |
| com.rk.editor.* | com.scto.mcs.core.editor.* |
| com.rk.lsp.* | com.scto.mcs.core.editor.lsp.* |
| com.rk.search.* | com.scto.mcs.core.editor.search.* |
| com.rk.tabs.* | com.scto.mcs.core.editor.tabs.* |
| com.rk.terminal.* | com.scto.mcs.core.terminal.* |
| com.rk.terminal.virtualkeys.* | com.scto.mcs.core.terminal.xed.virtualkeys.* |
| com.rk.git.* | com.scto.mcs.feature.git.* |
| com.rk.settings.* | com.scto.mcs.feature.settings.* |
| com.rk.components.compose.preferences.* | com.scto.mcs.core.ui.components.compose.preferences.* |
| com.rk.icons.* | com.scto.mcs.core.ui.icons.* |
| com.rk.color.* | com.scto.mcs.core.ui.color.* |
| com.rk.animations.* | com.scto.mcs.core.ui.animations.* |
## Schritt 1: Zentrale Build-Konfiguration & Version Catalog
**Aider-Aufruf:** /add build.gradle.kts settings.gradle.kts gradle/libs.versions.toml
 1. Überprüfe libs.versions.toml auf Vollständigkeit (Hilt, KSP, Compose).
 2. Stelle sicher, dass settings.gradle.kts alle Module (:core:* und :feature:*) korrekt inkludiert sind.
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
## Schritt 4: Quellcode-Refactoring (Feature-Module)
**Aider-Aufruf:** /add MCS_20260506_143519.md feature/**/*.kt feature/**/src/main/AndroidManifest.xml
**Anweisungen:**
 1. **Dynamische Analyse:** Nutze den Project Tree, um alle Feature-Module dynamisch zu durchlaufen. Migriere sie auf com.scto.mcs.feature.<modulname>.
 2. **Feature Settings (Zusammenführung & Refactoring):**
   * **XED Integration:** Arbeite die Funktionalität aus feature/settings/xed vollständig in das Hauptmodul feature/settings ein und lösche danach die xed Dateien.
   * **Namespace:** Setze Package auf com.scto.mcs.feature.settings.
   * **Import-Korrektur:** Nutze strikt die Globale Mapping Tabelle, um alle com.rk-Referenzen zu ersetzen.
## Schritt 5: Quellcode-Refactoring (Core-Submodule)
**Aider-Aufruf:** /add MCS_20260506_143519.md core/**/*.kt
**Anweisungen:**
 1. **Dynamische Ermittlung:** Nutze den Project Tree (MCS_20260506_143519.md), um alle Core-Submodule dynamisch zu identifizieren (eine manuelle Auflistung der Verzeichnisse entfällt).
 2. **Migration:** Führe für jedes identifizierte Core-Modul eine Migration auf com.scto.mcs.core.<modulname> durch.
 3. **Import-Korrektur:** Wende für alle Anpassungen ausnahmslos die Globale Mapping Tabelle an.
## Schritt 6: Ressourcen & Strings (Zentralisierung)
### 6.1 Ressourcen-Verschiebung (Core Submodule)
**Aider-Aufruf:** /add MCS_20260506_143519.md core/**/src/main/res/**/*.xml
**Anweisungen:**
 1. Verschiebe alle Ressourcen aus den im Tree gelisteten Core-Submodulen nach :core:resources (core/resources/src/main/res/).
### 6.2 Ressourcen-Verschiebung (Feature Submodule)
**Aider-Aufruf:** /add MCS_20260506_143519.md feature/**/src/main/res/**/*.xml
**Anweisungen:**
 1. Verschiebe alle Ressourcen aus den im Tree gelisteten Feature-Submodulen nach :core:resources (core/resources/src/main/res/).
### 6.3 String-Management & Code-Anpassung (Core Submodule)
**Aider-Aufruf:** /add MCS_20260506_143519.md core/**/src/main/java/**/*.kt core/**/src/main/java/**/*.java core/resources/src/main/res/values/strings.xml
**Anweisungen:**
 1. Betrifft alle Core-Submodule laut Tree **außer** termux-*.
 2. Führe alle strings.xml in der zentralen Datei zusammen (Duplikate entfernen).
 3. Ersetze hardkodierte UI-Strings im Code durch R.string-Referenzen.
 4. Aktualisiere alle Ressourcen-Imports auf com.scto.mcs.core.resources.R.
### 6.4 String-Management & Code-Anpassung (Feature Submodule)
**Aider-Aufruf:** /add MCS_20260506_143519.md feature/**/src/main/java/**/*.kt feature/**/src/main/java/**/*.java core/resources/src/main/res/values/strings.xml
**Anweisungen:**
 1. Betrifft alle Feature-Submodule **außer** dem Ordner feature/settings/xed (falls dieser noch nicht gelöscht wurde).
 2. Ziel-Datei: Modifiziere die zentrale Datei core/resources/src/main/res/values/strings.xml, indem du neue <string>-Elemente am Ende des <resources>-Blocks einfügst.
 3. Extrahiere hartkodierte UI-Strings (Ignoriere Log-Ausgaben, Keys etc.).
 4. Generiere eindeutige Keys (feature_<modulname>_<deskriptiver_name>).
 5. Ersetze die Strings in Compose (stringResource) oder Standard-Android (getString) unter Nutzung von com.scto.mcs.core.resources.R.
## Schritt 7: App-Modul & Finale Integration
**Aider-Aufruf:** /add MCS_20260506_143519.md app/build.gradle.kts app/src/main/AndroidManifest.xml
 1. Finalisiere :app auf com.scto.mcs.app.
 2. Validiere Manifest-Pfade (Activities, Services) und Permissions.
 3. Behebe letzte Kompilierfehler durch Namespace-Mismatch.
