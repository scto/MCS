MSC Projekt-Audit: Gradle & Abhängigkeiten
Dieses Dokument dient als Leitfaden für ein systematisches Audit der Build-Konfiguration des MSC-Projekts. Ziel ist die Konsistenz der Namespaces, Plugins und Modul-Abhängigkeiten.
Projekt-Struktur Referenz
* App: com.scto.msc.app
* Core: com.scto.msc.core.<submodule>
* Feature: com.scto.msc.feature.<submodule>
Schritt 1: Zentrale Konfiguration (Root & Catalog)
Befehl für Aider: aider build.gradle.kts settings.gradle.kts gradle/libs.versions.toml
Anweisungen:
1. Überprüfe libs.versions.toml auf Vollständigkeit (Android, Kotlin, Hilt, KSP, Compose).
2. Stelle sicher, dass die Plugin-Versionen im Root-build.gradle.kts aktuell und korrekt definiert sind.
3. Verifiziere in settings.gradle.kts, dass alle Module (core:*, feature:*) korrekt inkludiert sind.
Schritt 2: Audit der Core-Module
Befehl für Aider: aider core/*/build.gradle.kts
Anweisungen:
1. Prüfe jedes Core-Submodul auf den korrekten Namespace: com.scto.msc.core.<name>.
2. Stelle sicher, dass nur notwendige Plugins verwendet werden (z. B. com.android.library statt com.android.application).
3. Verifiziere die Hilt-Konfiguration (KSP/Kapt) in den Modulen.
4. Bereinige ungenutzte Imports oder falsche Modul-Referenzen innerhalb von :core.
Schritt 3: Audit der Feature-Module
Befehl für Aider: aider feature/*/build.gradle.kts
Anweisungen:
1. Prüfe die Namespaces: com.scto.msc.feature.<name>.
2. Stelle sicher, dass Features korrekt auf :core-Module zugreifen (via implementation(project(":core:<name>"))).
3. Aktiviere Compose-Compiler-Optionen, wo UI-Elemente vorhanden sind.
4. Prüfe, ob Feature-Module fälschlicherweise voneinander abhängen (sollte vermieden werden).
Schritt 4: App-Modul & Finale Integration
Befehl für Aider: aider app/build.gradle.kts app/src/main/AndroidManifest.xml
Anweisungen:
1. Namespace-Check: com.scto.msc.app.
2. Stelle sicher, dass alle benötigten :feature und :core Module als Abhängigkeiten gelistet sind.
3. Überprüfe die buildFeatures (Compose, ViewBinding etc.).
4. Gleiche das AndroidManifest.xml mit den Gradle-Definitionen ab (Permissions, Activity-Namespaces).
Schritt 5: Synchronisation & Cleanup
Befehl für Aider: aider **/build.gradle.kts
Anweisungen:
1. Führe eine finale Prüfung durch, um sicherzustellen, dass keine doppelten Abhängigkeiten oder Versions-Hardcoding (statt Version Catalog) vorhanden sind.
2. Korrigiere Warnungen bezüglich veralteter Gradle-Syntax.