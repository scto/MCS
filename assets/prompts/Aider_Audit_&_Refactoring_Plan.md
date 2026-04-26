MSC Projekt-Audit & Refactoring Plan
Dieses Dokument leitet aider durch den Prozess, das gesamte Projekt auf den Namespace com.scto.msc zu vereinheitlichen und die Gradle-Konfigurationen zu reparieren.
Globale Projekt-Architektur
* App: com.scto.msc.app
* Core: com.scto.msc.core.<submodule>
* Feature: com.scto.msc.feature.<submodule>
Schritt 1: Zentrale Konfiguration (Root & Version Catalog)
Aider Aufruf: aider build.gradle.kts settings.gradle.kts gradle/libs.versions.toml
Prompt:
Untersuche die Root-Konfiguration.
1. Überprüfe libs.versions.toml auf Vollständigkeit (Hilt, KSP, Compose, etc.).
2. Stelle sicher, dass settings.gradle.kts alle Module korrekt mit dem Schema :core:<name> und :feature:<name> inkludiert.
3. Korrigiere Plugin-Definitionen im Root-build.gradle.kts.
Schritt 2: Core-Module & Namespace Konsolidierung
Aider Aufruf: aider core/*/build.gradle.kts
Prompt:
Untersuche alle build.gradle.kts Dateien im Ordner core/.
1. Setze den Namespace jedes Moduls strikt auf com.scto.msc.core.<name>.
2. Stelle sicher, dass die compileSdk, minSdk und Java-Versionen aus dem Version Catalog oder konsistent gesetzt sind.
3. Bereinige die Abhängigkeiten: Core-Module sollten so wenig wie möglich voneinander abhängen.
4. Stelle sicher, dass Hilt/KSP korrekt konfiguriert ist.
Schritt 3: Feature-Module & Abhängigkeiten
Aider Aufruf: aider feature/*/build.gradle.kts
Prompt:
Untersuche alle build.gradle.kts Dateien im Ordner feature/.
1. Setze den Namespace jedes Moduls auf com.scto.msc.feature.<name>.
2. Stelle sicher, dass die Module korrekt auf die benötigten :core-Abhängigkeiten zugreifen.
3. Aktiviere Compose-Optionen in den Modulen, die UI-Komponenten enthalten.
Schritt 4: Globales Import-Refactoring (WICHTIG)
Aider Aufruf: aider **/*.kt **/*.java **/AndroidManifest.xml
Prompt:
Führe ein globales Refactoring aller Quellcodedateien durch, um die Imports auf den neuen Projekt-Namespace com.scto.msc anzupassen.
1. Ersetze alle Package-Deklarationen und Imports, die mit com.scto.mcs, com.srvhive oder com.rk beginnen, durch den entsprechenden Pfad unter com.scto.msc.
2. Aktualisiere alle R-Klassen-Imports (z.B. com.scto.msc.core.ui.R).
3. Überprüfe alle AndroidManifest.xml Dateien in den Submodulen und passe die package-Attribute oder Activity-Pfade an.
4. Stelle sicher, dass keine alten Referenzen auf mcs oder srvhive im Code verbleiben.
Schritt 5: App-Modul & Finale Integration
Aider Aufruf: aider app/build.gradle.kts app/src/main/AndroidManifest.xml
Prompt:
Finalisiere das app Modul (com.scto.msc.app).
1. Stelle sicher, dass alle :feature und :core Module korrekt inkludiert sind.
2. Überprüfe das Haupt-Manifest auf korrekte Permissions und die Start-Activity.
3. Korrigiere alle verbleibenden Build-Fehler, die durch das Umbenennen der Packages entstanden sind.