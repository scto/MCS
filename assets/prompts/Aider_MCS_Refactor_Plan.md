MCS Projekt-Refactoring & Ressourcen-Zentralisierungs Plan (Final)
Dieses Dokument dient als Master-Plan für die Konsolidierung des Projekts auf den Namespace com.scto.mcs, die Bereinigung der Gradle-Konfigurationen und die Zentralisierung aller Ressourcen.
Projekt-Kontext
* Ziel-Package: com.scto.mcs
* Struktur:
   * App: com.scto.mcs.app
   * Core: com.scto.mcs.core.<submodule>
   * Feature: com.scto.mcs.feature.<submodule>
* Zu ersetzende Namespaces: com.rk, com.srvhive, com.scto.msc

Schritt 1: Zentrale Build-Konfiguration & Version Catalog
Aider-Aufruf: /add build.gradle.kts settings.gradle.kts gradle/libs.versions.toml
Anweisungen:
1. Überprüfung der zentralen Build-Dateien: Untersuche libs.versions.toml auf Vollständigkeit bezüglich Android Plugins, Kotlin, Hilt, Compose und KSP.
2. Synchronisation der Modul-Inklusion: Stelle sicher, dass in settings.gradle.kts alle Module (:core:* und :feature:*) korrekt inkludiert sind.
3. Validierung der Root-Plugin-Definitionen: Überprüfe das Root-build.gradle.kts auf korrekte Plugin-Definitionen ohne Versionen.
4. Bereinigung harter Versions-Strings: Verifiziere, dass keine harten Versions-Strings in den Gradle-Dateien stehen.

Schritt 2: Audit der Core-Module (Build-Logik)
Aider-Aufruf: /add core/**/build.gradle.kts
Anweisungen:
1. Überarbeitung der Build-Gradle Dateien (Core): Setze in jedem Modul unter :core den Namespace strikt auf com.scto.mcs.core.<modulname>.
2. Standardisierung der Hilt/KSP Konfiguration: Konfiguriere Hilt und KSP einheitlich für alle Core-Module gemäß dem Projektstandard.

Schritt 3: Audit der Feature-Module (Build-Logik)
Aider-Aufruf: /add feature/**/build.gradle.kts
Anweisungen:
1. Überarbeitung der Build-Gradle Dateien (Feature): Setze in jedem Modul unter :feature den Namespace strikt auf com.scto.mcs.feature.<modulname>.
2. Verknüpfung der Core-Abhängigkeiten: Stelle sicher, dass die Feature-Module korrekt auf die benötigten :core-Module zugreifen (via implementation(project(":core:<name>"))).

Schritt 4: Quellcode-Refactoring (Feature-Module)

4.1 Feature Editor
Aider-Aufruf: /add feature/editor/**/*.kt feature/editor/src/main/AndroidManifest.xml Anweisungen: Migration auf com.scto.mcs.feature.editor. Ersetze com.rk, com.srvhive, com.scto.msc.

4.2 Feature Git
Aider-Aufruf: /add feature/git/**/*.kt feature/git/src/main/AndroidManifest.xml Anweisungen: Migration auf com.scto.mcs.feature.git. Ersetze com.rk, com.srvhive, com.scto.msc.

4.3 Feature Settings
Aider-Aufruf: /add feature/settings/**/*.kt feature/settings/src/main/AndroidManifest.xml Anweisungen: Migration auf com.scto.mcs.feature.settings. Ersetze com.rk, com.srvhive, com.scto.msc.

4.4 Feature Terminal
Aider-Aufruf: /add feature/terminal/**/*.kt feature/terminal/src/main/AndroidManifest.xml Anweisungen: Migration auf com.scto.mcs.feature.terminal. Ersetze com.rk, com.srvhive, com.scto.msc.

Schritt 5: Quellcode-Refactoring (Core-Submodule)

5.1 Core DI
Aider-Aufruf: /add core/di/**/*.kt Anweisungen: Migration auf com.scto.mcs.core.di.

5.2 Core Exec
Aider-Aufruf: /add core/exec/**/*.kt Anweisungen: Migration auf com.scto.mcs.core.exec.

5.3 Core Files
Aider-Aufruf: /add core/files/**/*.kt Anweisungen: Migration auf com.scto.mcs.core.files.

5.4 Core Navigation
Aider-Aufruf: /add core/navigation/**/*.kt Anweisungen: Migration auf com.scto.mcs.core.navigation.

5.5 Core Network
Aider-Aufruf: /add core/network/**/*.kt Anweisungen: Migration auf com.scto.mcs.core.network.

5.6 Core Resources
Aider-Aufruf: /add core/resources/**/*.kt Anweisungen: Migration auf com.scto.mcs.core.resources.

5.7 Core UI
Aider-Aufruf: /add core/ui/**/*.kt Anweisungen: Migration auf com.scto.mcs.core.ui.

5.8 Core Utils
Aider-Aufruf: /add core/utils/**/*.kt Anweisungen: Migration auf com.scto.mcs.core.utils.

5.9 Core Terminal (Logik)
Aider-Aufruf: /add core/terminal/**/*.kt Anweisungen: Migration auf com.scto.mcs.core.terminal.

Schritt 6: Zentralisierung der Ressourcen (Aufgeteilt)

Schritt 6.1: Ressourcen-Verschiebung (Filesystem)
Aider-Aufruf: /add **/*.xml Anweisungen: Verschiebe alle Ressourcen aus allen Modulen nach :core:resources unter core/resources/src/main/res/. Behalte die Unterordnerstruktur bei.

Schritt 6.2: String-Management & Code-Anpassung
Aider-Aufruf: /add core/resources/src/main/res/values/*.xml **/*.kt **/*.java Anweisungen:
1. Führe alle strings.xml in der zentralen Datei zusammen (Duplikate entfernen).
2. Ersetze hartkodierte UI-Strings im Code durch R.string-Referenzen.
3. Aktualisiere alle Ressourcen-Imports projektweit auf com.scto.mcs.core.resources.R.
4. Lösche leere res-Ordner in allen Modulen außer :core:resources.

Schritt 7: App-Modul & Finale Integration
Aider-Aufruf: /add app/build.gradle.kts app/src/main/AndroidManifest.xml Anweisungen:
1. Finalisiere :app auf com.scto.mcs.app.
2. Validiere Manifest-Pfade und Permissions.
3. Behebe letzte Kompilierfehler.