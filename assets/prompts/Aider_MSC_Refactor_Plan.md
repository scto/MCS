MCS Projekt-Refactoring & Ressourcen-Zentralisierungs Plan
Dieses Dokument dient als Master-Plan für die Konsolidierung des Projekts auf den Namespace com.scto.mcs, die Bereinigung der Gradle-Konfigurationen und die Zentralisierung aller Ressourcen.
Projekt-Kontext
* Ziel-Package: com.scto.mcs
* Struktur:
   * App: com.scto.mcs.app
   * Core: com.scto.mcs.core.<submodule>
   * Feature: com.scto.mcs.feature.<submodule>
* Zu ersetzende Namespaces: com.rk, com.srvhive, com.scto.mcs

Schritt 1: Zentrale Build-Konfiguration & Version Catalog
Aider-Aufruf: aider build.gradle.kts settings.gradle.kts gradle/libs.versions.toml
Anweisungen:
1. Überprüfung der zentralen Build-Dateien: Untersuche libs.versions.toml auf Vollständigkeit bezüglich Android Plugins, Kotlin, Hilt, Compose und KSP.
2. Synchronisation der Modul-Inklusion: Stelle sicher, dass in settings.gradle.kts alle Module (:core:* und :feature:*) korrekt inkludiert sind.
3. Validierung der Root-Plugin-Definitionen: Überprüfe das Root-build.gradle.kts auf korrekte Plugin-Definitionen ohne Versionen.
4. Bereinigung harter Versions-Strings: Verifiziere, dass keine harten Versions-Strings in den Gradle-Dateien stehen.

Schritt 2: Audit der Core-Module (Build-Logik)
Aider-Aufruf: aider core/*/build.gradle.kts
Anweisungen:
1. Überarbeitung der Build-Gradle Dateien (Core): Setze in jedem Modul unter :core den Namespace strikt auf com.scto.mcs.core.<modulname>.
2. Standardisierung der Hilt/KSP Konfiguration: Konfiguriere Hilt und KSP einheitlich für alle Core-Module gemäß dem Projektstandard.

Schritt 3: Audit der Feature-Module (Build-Logik)
Aider-Aufruf: aider feature/*/build.gradle.kts
Anweisungen:
1. Überarbeitung der Build-Gradle Dateien (Feature): Setze in jedem Modul unter :feature den Namespace strikt auf com.scto.mcs.feature.<modulname>.
2. Verknüpfung der Core-Abhängigkeiten: Stelle sicher, dass die Feature-Module korrekt auf die benötigten :core-Module zugreifen (via implementation(project(":core:<name>"))).

Schritt 4: Quellcode-Refactoring (Feature-Module)
4.1 Refactoring des Quellcodes (Feature-Editor)
Aider-Aufruf: aider feature/editor/**/*.kt feature/editor/src/main/AndroidManifest.xml Anweisungen: Ändere Package-Deklarationen und Imports von com.rk, com.srvhive oder com.scto.mcs zu com.scto.mcs.feature.editor.
4.2 Refactoring des Quellcodes (Feature-Git)
Aider-Aufruf: aider feature/git/**/*.kt feature/git/src/main/AndroidManifest.xml Anweisungen: Ändere Package-Deklarationen und Imports zu com.scto.mcs.feature.git.
4.3 Refactoring des Quellcodes (Feature-Settings)
Aider-Aufruf: aider feature/settings/**/*.kt feature/settings/src/main/AndroidManifest.xml Anweisungen: Ändere Package-Deklarationen und Imports zu com.scto.mcs.feature.settings.
4.4 Refactoring des Quellcodes (Feature-Terminal)
Aider-Aufruf: aider feature/terminal/**/*.kt feature/terminal/src/main/AndroidManifest.xml Anweisungen: Ändere Package-Deklarationen und Imports zu com.scto.mcs.feature.terminal.

Schritt 5: Quellcode-Refactoring (Core-Submodule)
5.1 Refactoring Core-DI
Aider-Aufruf: aider core/di/**/*.kt Anweisungen: Migration auf com.scto.mcs.core.di.
5.2 Refactoring Core-Exec
Aider-Aufruf: aider core/exec/**/*.kt Anweisungen: Migration auf com.scto.mcs.core.exec.
5.3 Refactoring Core-Files
Aider-Aufruf: aider core/files/**/*.kt Anweisungen: Migration auf com.scto.mcs.core.files.
5.4 Refactoring Core-Navigation
Aider-Aufruf: aider core/navigation/**/*.kt Anweisungen: Migration auf com.scto.mcs.core.navigation.
5.5 Refactoring Core-Network
Aider-Aufruf: aider core/network/**/*.kt Anweisungen: Migration auf com.scto.mcs.core.network.
5.6 Refactoring Core-Resources
Aider-Aufruf: aider core/resources/**/*.kt Anweisungen: Migration auf com.scto.mcs.core.resources.
5.7 Refactoring Core-UI
Aider-Aufruf: aider core/ui/**/*.kt Anweisungen: Migration auf com.scto.mcs.core.ui.
5.8 Refactoring Core-Utils
Aider-Aufruf: aider core/utils/**/*.kt Anweisungen: Migration auf com.scto.mcs.core.utils.
5.9 Refactoring Core-Terminal (Logik)
Aider-Aufruf: aider core/terminal/**/*.kt Anweisungen: Migration auf com.scto.mcs.core.terminal.

Schritt 6: Zentralisierung der Ressourcen & String-Management
Aider-Aufruf: aider **/*.xml **/*.kt **/*.java
Anweisungen:
1. Zentralisierung aller Ressourcen: Verschiebe alle Ressourcen (drawables, layouts, values, xml, etc.) aus allen Modulen und Submodulen in das Modul :core:resources.
2. Zusammenführung der strings.xml: Sammle alle strings.xml Dateien aus dem gesamten Projekt und führe sie in core/resources/src/main/res/values/strings.xml zusammen. Eliminiere dabei Duplikate.
3. Ersetzung hardkodierter Strings: Scanne alle Kotlin- und Java-Dateien nach hardkodierten User-Interface-Strings. Ersetze diese durch Referenzen auf die neue zentrale strings.xml (z.B. getString(R.string...) oder context.getString(...)).
4. Bereinigung der Module: Lösche alle verbleibenden res-Ordner und deren Inhalte in allen Modulen (App, Features, andere Cores), außer im Modul :core:resources.
5. R-Klassen-Korrektur: Stelle sicher, dass alle Code-Dateien nun com.scto.mcs.core.resources.R importieren, um auf Ressourcen zuzugreifen.

Schritt 7: App-Modul & Finale Integration
Aider-Aufruf: aider app/build.gradle.kts app/src/main/AndroidManifest.xml
Anweisungen:
1. Finalisierung des App-Namespace: Setze den Namespace des :app Moduls auf com.scto.mcs.app.
2. Validierung der Manifest-Konfiguration: Überprüfe die Haupt-AndroidManifest.xml auf korrekte Pfade zu Activities und stelle sicher, dass alle Permissions (Internet, Storage etc.) vorhanden sind.
3. Abschließende Fehlerbehebung: Behebe alle verbleibenden Kompilierfehler, die durch die Namespace-Verschiebungen, Import-Änderungen oder Ressourcen-Umzüge entstanden sind.