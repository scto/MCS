MSC Projekt-Refactoring & Build-Audit Plan
Dieses Dokument dient als Master-Plan für die Konsolidierung des Projekts auf den Namespace com.scto.msc und die Bereinigung der Gradle-Konfigurationen in allen Modulen.
Projekt-Kontext
* Ziel-Package: com.scto.msc
* Struktur:
   * App: com.scto.msc.app
   * Core: com.scto.msc.core.<submodule>
   * Feature: com.scto.msc.feature.<submodule>
* Zu ersetzende Namespaces: com.rk, com.srvhive, com.scto.mcs

Schritt 1: Zentrale Build-Konfiguration & Version Catalog
Aider-Aufruf: aider build.gradle.kts settings.gradle.kts gradle/libs.versions.toml
Anweisungen:
1. Überprüfung der zentralen Build-Dateien: Untersuche libs.versions.toml auf Vollständigkeit bezüglich Android Plugins, Kotlin, Hilt, Compose und KSP.
2. Synchronisation der Modul-Inklusion: Stelle sicher, dass in settings.gradle.kts alle Module (:core:* und :feature:*) korrekt inkludiert sind.
3. Validierung der Root-Plugin-Definitionen: Überprüfe das Root-build.gradle.kts auf korrekte Plugin-Definitionen ohne Versionen (Nutzung des Version Catalogs).
4. Bereinigung harter Versions-Strings: Verifiziere, dass keine harten Versions-Strings in den Gradle-Dateien stehen und alles konsistent über den Version Catalog läuft.

Schritt 2: Audit der Core-Module (Namespaces & Gradle)
Aider-Aufruf: aider core/*/build.gradle.kts
Anweisungen:
1. Überarbeitung der Build-Gradle Dateien (Core): Setze in jedem Modul unter :core den Namespace strikt auf com.scto.msc.core.<modulname>.
2. Optimierung von Plugins und Imports: Entferne ungenutzte Plugins und unnötige Imports aus den Build-Dateien der Core-Submodule.
3. Strukturierung der Modul-Abhängigkeiten: Stelle sicher, dass die Abhängigkeiten zwischen Core-Modulen korrekt definiert sind und keine zirkulären Abhängigkeiten existieren.
4. Standardisierung der Hilt/KSP Konfiguration: Konfiguriere Hilt und KSP einheitlich für alle Core-Module gemäß dem Projektstandard.

Schritt 3: Audit der Feature-Module (Namespaces & Gradle)
Aider-Aufruf: aider feature/*/build.gradle.kts
Anweisungen:
1. Überarbeitung der Build-Gradle Dateien (Feature): Setze in jedem Modul unter :feature den Namespace strikt auf com.scto.msc.feature.<modulname>.
2. Verknüpfung der Core-Abhängigkeiten: Stelle sicher, dass die Feature-Module korrekt auf die benötigten :core-Module zugreifen (via implementation(project(":core:<name>"))).
3. Konfiguration der Compose-Optionen: Aktiviere Compose-Optionen und den Compose-Compiler in den Modulen, die UI-Elemente enthalten.
4. Angleichung der SDK-Versionen: Harmonisiere die minSdk und targetSdk Werte über alle Feature-Module hinweg basierend auf den zentralen Vorgaben.

Schritt 4: Globales Quellcode-Refactoring (Imports & Packages)
Aider-Aufruf: aider **/*.kt **/*.java **/AndroidManifest.xml
Anweisungen (WICHTIG):
1. Projektweite Anpassung der Package-Deklarationen: Führe eine globale Suche und Ersetzung aller Package-Deklarationen in allen Quellcodedateien des Projekts durch.
2. Migration der Legacy-Namespaces: Ändere alle Package-Strings von com.rk, com.srvhive oder com.scto.mcs konsistent zu com.scto.msc.
3. Refactoring der Quellcode-Imports: Aktualisiere alle Imports in allen .kt und .java Dateien, um auf die neuen Pfade unter com.scto.msc zu zeigen.
4. Korrektur der R-Klassen-Referenzen: Achte besonders auf die generierten R-Klassen-Imports und passe diese an den neuen Namespace an (z.B. com.scto.msc.core.ui.R).
5. Aktualisierung der Manifest-Namespaces: Passe alle package-Attribute und Pfad-Referenzen in den AndroidManifest.xml Dateien aller Submodule an.

Schritt 5: App-Modul, Manifeste & Finale Integration
Aider-Aufruf: aider app/build.gradle.kts app/src/main/AndroidManifest.xml
Anweisungen:
1. Finalisierung des App-Modul Namespaces: Setze den Namespace des :app Moduls auf com.scto.msc.app.
2. Integration der Feature-Module: Stelle sicher, dass alle Feature-Module im App-Modul korrekt als Abhängigkeiten registriert sind.
3. Validierung der Manifest-Konfiguration: Überprüfe die Haupt-AndroidManifest.xml auf korrekte Pfade zu Activities, Services und Providern sowie das Vorhandensein aller Permissions (Internet, Storage etc.).
4. Abschließende Fehlerbehebung: Identifiziere und behebe alle verbleibenden Kompilierfehler, die durch die Namespace-Verschiebungen oder geänderten Imports entstanden sind.