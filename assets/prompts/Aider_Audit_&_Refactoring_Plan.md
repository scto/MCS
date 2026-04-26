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
1. Untersuche die libs.versions.toml auf Vollständigkeit (Android Plugins, Kotlin, Hilt, Compose, KSP).
2. Stelle sicher, dass in settings.gradle.kts alle Module (:core:* und :feature:*) korrekt inkludiert sind.
3. Überprüfe das Root-build.gradle.kts auf korrekte Plugin-Definitionen ohne Versionen (da diese im Catalog stehen sollten).
4. Verifiziere, dass keine harten Versions-Strings in den Gradle-Dateien stehen.
Schritt 2: Audit der Core-Module (Namespaces & Gradle)
Aider-Aufruf: aider core/*/build.gradle.kts
Anweisungen:
1. Setze in jedem Modul unter :core den Namespace strikt auf com.scto.msc.core.<modulname>.
2. Bereinige ungenutzte Plugins und Imports.
3. Stelle sicher, dass die Abhängigkeiten zwischen Core-Modulen korrekt sind (keine zirkulären Abhängigkeiten).
4. Konfiguriere Hilt/KSP einheitlich für alle Core-Module.
Schritt 3: Audit der Feature-Module (Namespaces & Gradle)
Aider-Aufruf: aider feature/*/build.gradle.kts
Anweisungen:
1. Setze in jedem Modul unter :feature den Namespace strikt auf com.scto.msc.feature.<modulname>.
2. Verknüpfe die notwendigen :core-Module korrekt via implementation(project(":core:<name>")).
3. Aktiviere Compose-Optionen, falls UI-Elemente vorhanden sind.
4. Harmonisiere die minSdk und targetSdk Werte über alle Feature-Module hinweg.
Schritt 4: Globales Quellcode-Refactoring (Imports & Packages)
Aider-Aufruf: aider **/*.kt **/*.java **/AndroidManifest.xml
Anweisungen (WICHTIG):
1. Führe eine globale Suche und Ersetzung aller Package-Deklarationen durch.
2. Ändere alle Package-Strings von com.rk, com.srvhive oder com.scto.mcs zu com.scto.msc.
3. Aktualisiere alle Imports in allen .kt und .java Dateien, um auf den neuen com.scto.msc Pfad zu zeigen.
4. Achte besonders auf die generierten R-Klassen-Imports (z.B. von com.srvhive.app.R zu com.scto.msc.app.R).
5. Passe alle package-Attribute in den AndroidManifest.xml Dateien der Submodule an.
Schritt 5: App-Modul, Manifeste & Finale Integration
Aider-Aufruf: aider app/build.gradle.kts app/src/main/AndroidManifest.xml
Anweisungen:
1. Finalisiere das :app Modul mit dem Namespace com.scto.msc.app.
2. Stelle sicher, dass alle Feature-Module im App-Modul inkludiert sind.
3. Überprüfe die AndroidManifest.xml der App:
   * Korrigiere die Pfade zu Activities, Services und Providern.
   * Stelle sicher, dass alle Permissions (Internet, Storage für Terminal etc.) vorhanden sind.
4. Behebe alle verbleibenden Kompilierfehler, die durch verschobene Klassen oder geänderte Imports entstanden sind.