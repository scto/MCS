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
2. Stelle sicher, dass settings.gradle.kts alle Module (:core:* und :feature:*) korrekt inkludiert sind.
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
1. XED Integration: Arbeite die Funktionalität aus feature/settings/xed vollständig in das Hauptmodul feature/settings ein und lösche danach die xed Dateien.
2. Namespace: Setze Package auf com.scto.mcs.feature.settings.
3. Import-Korrektur: Nutze strikt die Globale Mapping Tabelle, um alle com.rk-Referenzen zu ersetzen.

4.4 Feature Terminal
Aider-Aufruf: /add feature/terminal/**/*.kt feature/terminal/src/main/AndroidManifest.xml Anweisungen: Migration auf com.scto.mcs.feature.terminal. Nutze die Globale Mapping Tabelle.

Schritt 5: Quellcode-Refactoring (Core-Submodule)
Migration auf com.scto.mcs.core.<name> unter Verwendung der Mapping-Tabelle.

* 5.1 bis 5.9: Refactoring von DI, Exec, Files, Navigation, Network, Resources, UI, Utils und Terminal-Logik.

Schritt 6: Ressourcen & Strings (Zentralisierung)
6.1 Ressourcen-Verschiebung (Core Submodule)
Aider-Aufruf: /add core/**/src/main/res/**/*.xml Anweisungen: Verschiebe alle Ressourcen aus Core-Submodulen nach :core:resources.

6.2 Ressourcen-Verschiebung (Feature Submodule)
Aider-Aufruf: /add feature/**/src/main/res/**/*.xml Anweisungen: Verschiebe alle Ressourcen aus Feature-Submodulen nach :core:resources.

6.3 String-Management & Code-Anpassung (Core Submodule)
Anweisungen: Betrifft Core (außer termux-*). Zusammenführung in core/resources/src/main/res/values/strings.xml.

6.4 String-Management & Code-Anpassung (Feature Submodule)
Aider-Aufruf: /add feature/**/src/main/java/**/*.kt feature/**/src/main/java/**/*.java core/resources/src/main/res/values/strings.xml
Detaillierte Anweisungen für Aider:
1. Ziel-Datei: Modifiziere die zentrale Datei core/resources/src/main/res/values/strings.xml, indem du neue <string>-Elemente am Ende des <resources>-Blocks einfügst.
2. Ausschluss: Ignoriere alle Dateien im Verzeichnis feature/settings/xed.
3. String-Extraktion:
   * Suche in allen .kt und .java Dateien der Feature-Module nach hartkodierten Strings, die im User-Interface sichtbar sind (z.B. Button-Labels, Dialog-Texte, Toasts).
   * Ignoriere: Log-Ausgaben (Log.d, println), interne Tags, Datenbank-Keys und technische Konstanten.
4. Schlüssel-Generierung: Erstelle für jeden extrahierten String einen eindeutigen Namen im Format feature_<modulname>_<deskriptiver_name> (z.B. feature_terminal_start_session).
5. Code-Ersetzung:
   * In Compose: Ersetze "Text" durch stringResource(id = R.string.key).
   * In Standard-Android: Ersetze "Text" durch context.getString(R.string.key) oder getString(R.string.key).
   * Nutze immer com.scto.mcs.core.resources.R für den Ressourcen-Zugriff.
6. Import-Fix: Entferne alle lokalen R-Imports in den Feature-Modulen und ersetze sie durch import com.scto.mcs.core.resources.R.
 
Schritt 7: App-Modul & Finale Integration
Aider-Aufruf: /add app/build.gradle.kts app/src/main/AndroidManifest.xml
1. Finalisiere :app auf com.scto.mcs.app.
2. Behebe letzte Kompilierfehler.