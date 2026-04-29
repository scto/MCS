MCS Projekt-Refactoring & Ressourcen-Zentralisierungs Plan (Final)
Dieses Dokument dient als Master-Plan für die Konsolidierung des Projekts auf den Namespace com.scto.mcs. Es umfasst das Audit der Gradle-Konfigurationen, die Zentralisierung der Ressourcen und ein vollständiges Refactoring der Imports.
Projekt-Kontext
* Ziel-Package: com.scto.mcs
* Struktur:
   * App: com.scto.mcs.app
   * Core: com.scto.mcs.core.<submodule>
   * Feature: com.scto.mcs.feature.<submodule>
* Zu ersetzende Namespaces: com.rk, com.srvhive, com.scto.msc
Globale Import-Mapping Tabelle (Alt -> Neu)
Aider soll bei jedem Refactoring-Schritt diese Tabelle nutzen, um alte Referenzen zu ersetzen:
Alter Pfad (com.rk / com.srvhive)
	Neuer Pfad (com.scto.mcs)
	com.rk.App
	com.scto.mcs.app.App
	com.rk.activities.main.MainActivity
	com.scto.mcs.app.ui.activities.main.MainActivity
	com.rk.activities.settings.SettingsActivity
	com.scto.mcs.app.ui.activities.settings.SettingsActivity
	com.rk.activities.terminal.Terminal
	com.scto.mcs.app.ui.activities.terminal.TerminalActivity
	com.rk.file.*
	com.scto.mcs.core.files.*
	com.rk.utils.*
	com.scto.mcs.core.utils.*
	com.rk.resources.*
	com.scto.mcs.core.resources.*
	com.rk.editor.*
	com.scto.mcs.core.editor.*
	com.rk.lsp.*
	com.scto.mcs.core.editor.lsp.*
	com.rk.search.*
	com.scto.mcs.core.editor.search.*
	com.rk.tabs.*
	com.scto.mcs.core.editor.tabs.*
	com.rk.terminal.*
	com.scto.mcs.core.terminal.*
	com.rk.terminal.virtualkeys.*
	com.scto.mcs.core.terminal.virtualkeys.*
	com.rk.git.*
	com.scto.mcs.feature.git.*
	com.rk.settings.*
	com.scto.mcs.feature.settings.*
	com.rk.components.compose.preferences.*
	com.scto.mcs.core.ui.components.compose.preferences.*
	com.rk.icons.*
	com.scto.mcs.core.ui.icons.*
	com.rk.color.*
	com.scto.mcs.core.ui.color.*
	com.rk.animations.*
	com.scto.mcs.core.ui.animations.*

	Schritt 1: Zentrale Build-Konfiguration & Version Catalog
Aider-Aufruf: /add build.gradle.kts settings.gradle.kts gradle/libs.versions.toml
1. Überprüfe libs.versions.toml auf Vollständigkeit (Hilt, KSP, Compose).
2. Stelle sicher, dass settings.gradle.kts alle Module (:core:* und :feature:*) korrekt inkludiert.
3. Bereinige harte Versions-Strings in allen Gradle-Dateien.

Schritt 2: Audit der Core-Module (Build-Logik)
Aider-Aufruf: /add core/**/build.gradle.kts
1. Setze in jedem Modul unter :core den Namespace auf com.scto.mcs.core.<modulname>.
2. Konfiguriere Hilt und KSP einheitlich.

Schritt 3: Audit der Feature-Module (Build-Logik)
Aider-Aufruf: /add feature/**/build.gradle.kts
1. Setze in jedem Modul unter :feature den Namespace auf com.scto.mcs.feature.<modulname>.
2. Verknüpfe die notwendigen :core-Module korrekt via implementation(project(":core:<name>")).

Schritt 4: Quellcode-Refactoring (Feature-Module)

4.1 Feature Editor
Aider-Aufruf: /add feature/editor/**/*.kt feature/editor/src/main/AndroidManifest.xml Anweisungen: Migration auf com.scto.mcs.feature.editor. Nutze die Globale Mapping Tabelle.

4.2 Feature Git
Aider-Aufruf: /add feature/git/**/*.kt feature/git/src/main/AndroidManifest.xml Anweisungen: Migration auf com.scto.mcs.feature.git. Nutze die Globale Mapping Tabelle.

4.3 Feature Settings (Zusammenführung & Refactoring)
Aider-Aufruf: /add feature/settings/**/*.kt feature/settings/src/main/AndroidManifest.xml Anweisungen:
1. XED Integration: Arbeite die Funktionalität aus feature/settings/xed vollständig in das Hauptmodul feature/settings ein und lösche die xed Dateien.
2. Namespace: Setze Package auf com.scto.mcs.feature.settings.
3. Import-Korrektur: Nutze strikt die Globale Mapping Tabelle, um alle com.rk-Referenzen zu ersetzen.

4.4 Feature Terminal
Aider-Aufruf: /add feature/terminal/**/*.kt feature/terminal/src/main/AndroidManifest.xml Anweisungen: Migration auf com.scto.mcs.feature.terminal. Nutze die Globale Mapping Tabelle.

Schritt 5: Quellcode-Refactoring (Core-Submodule)
Für alle Schritte gilt: Migration auf com.scto.mcs.core.<name> unter Verwendung der Mapping-Tabelle.

* 5.1 DI: /add core/di/**/*.kt

* 5.2 Exec: /add core/exec/**/*.kt

* 5.3 Files: /add core/files/**/*.kt

* 5.4 Navigation: /add core/navigation/**/*.kt

* 5.5 Network: /add core/network/**/*.kt

* 5.6 Resources: /add core/resources/**/*.kt

* 5.7 UI: /add core/ui/**/*.kt

* 5.8 Utils: /add core/utils/**/*.kt

* 5.9 Terminal (Logik): /add core/terminal/**/*.kt

Schritt 6: Ressourcen & Strings

6.1 Ressourcen-Verschiebung
Aider-Aufruf: /add **/*.xml
1. Verschiebe alle Ressourcen aus allen Modulen nach :core:resources (core/resources/src/main/res/).

6.2 String-Management & Code-Anpassung
Aider-Aufruf: /add core/resources/src/main/res/values/*.xml **/*.kt **/*.java
1. Führe alle strings.xml zentral zusammen (Duplikate entfernen).
2. Ersetze hardkodierte UI-Strings durch R.string-Referenzen.
3. Aktualisiere alle Ressourcen-Imports projektweit auf com.scto.mcs.core.resources.R.

Schritt 7: App-Modul & Finale Integration
Aider-Aufruf: /add app/build.gradle.kts app/src/main/AndroidManifest.xml
1. Finalisiere :app auf com.scto.mcs.app.
2. Validiere Manifest-Pfade (Activities, Services) und Permissions.
3. Behebe letzte Kompilierfehler durch Namespace-Mismatch.