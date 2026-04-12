MCS: Atomare Aider-Arbeitsschritte (Master-Plan)
Dieses Dokument enthält präzise, kleinteilige Prompts für Aider. Führe nach jedem Schritt einen Gradle-Sync durch.
🛠 Teil 1: Infrastruktur & Gradle (Root-Ebene)
Schritt 1.1: Version Catalog Fix (Vermeidung von Build-Fehlern)
Prompt:
Aktualisiere 'gradle/libs.versions.toml'. Nutze stabile Plugin-Versionen, um Metadaten-Fehler zu vermeiden:
[versions]
agp = "8.7.2"
kotlin = "2.2.0"
hilt = "2.51"
soraEditor = "0.23.0"
jgit = "6.8.0.202311291450-r"
composeBom = "2024.10.01"

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
android-library = { id = "com.android.library", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-kapt = { id = "org.jetbrains.kotlin.kapt", version.ref = "kotlin" }
hilt-android = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }

Schritt 1.2: Root-Datei Schutz
Prompt:
Überschreibe die Root-Datei 'build.gradle.kts'. Sie darf ausschließlich Plugin-Deklarationen enthalten (apply false). Entferne alle 'android { }' oder 'dependencies { }' Blöcke aus dieser Datei.

Schritt 1.3: Explizites Pfad-Mapping
Prompt:
Aktualisiere 'settings.gradle.kts'. Implementiere eine Schleife für 'core' und 'feature' Module, die 'projectDir' explizit auf den physischen Pfad setzt (z.B. file("core/data")). Stelle sicher, dass ':core:common' NICHT enthalten ist.

📱 Teil 2: App Modul & Basis-Code
Schritt 2.1: App-Modul Gradle
Prompt:
Erstelle 'app/build.gradle.kts' mit Namespace 'com.scto.mcs', compileSdk 36 und aktiviere 'buildFeatures.compose = true'. Binde 'hilt-android', 'kapt' und die Compose-Basis-Bibliotheken ein.

Schritt 2.2: Hilt-App & Manifest
Prompt:
1. Erstelle 'app/src/main/java/com/scto/mcs/MCSApplication.kt' (@HiltAndroidApp).
2. Erstelle 'app/src/main/AndroidManifest.xml' mit den richtigen Referenzen auf Icons und Label (vorbereitet für das Ressourcen-Modul).

🧠 Teil 3: Core Manager (Hilt-Singletons)
Schritt 3.1: Terminal Environment
Prompt:
Erstelle 'core/terminal/src/main/java/com/scto/mcs/core/terminal/TerminalEnvironment.kt'. Implementiere die Ordner-Initialisierung in 'filesDir' und verwalte die Pfade für JDK/SDK. Erstelle das Hilt-Modul im 'di' Paket.

📦 Teil 8: Ressourcen-Zentralisierung (Kahlschlag-Modus)
Schritt 8.1: Dateitransfer & Namenskonflikte
Prompt:
1. Verschiebe alle Ressourcen aus 'app/src/main/res/', 'core/*/src/main/res/' und 'feature/*/src/main/res/' nach 'core/resourcess/src/main/res/'.
2. Regel: Falls Dateinamen kollidieren, hänge den Modulnamen als Präfix an (z.B. 'setup_strings.xml').

Schritt 8.2: Intelligentes XML-Merge
Prompt:
Führe alle Inhalte von 'strings.xml' und 'colors.xml' aus dem gesamten Projekt in 'core/resourcess/src/main/res/values/' zusammen. Nutze bei ID-Konflikten Modul-Präfixe (z.B. 'setup_app_name'). Lösche keine Daten beim Mergen.

Schritt 8.3: Ressourcen-Purge & Verkabelung
Prompt:
1. Lösche restlos alle 'res/' Ordner in allen Modulen (app, core, feature), AUSSER in 'core/resourcess/'.
2. Füge 'implementation(project(":core:resourcess"))' in ALLE 'build.gradle.kts' Dateien ein.

Schritt 8.4: Globales R-Import Refactoring
Prompt:
Durchsuche den gesamten Kotlin-Sourcecode. Ersetze alle Importe von 'com.scto.mcs.R' (oder ähnliche) durch 'import com.scto.mcs.core.resourcess.R'. Korrigiere Referenzen im Code, falls IDs beim Mergen umbenannt wurden.

🔍 Teil 100: Abschluss-Audit
Schritt 100.1: Build-Validation
Prompt:
Überprüfe alle 'build.gradle.kts' Dateien. Stelle sicher, dass jedes Modul den richtigen Namespace hat, keine lokalen Ressourcen besitzt und korrekt auf ':core:resourcess' zugreift. Verifiziere, dass keine Android-Blöcke in der Root-Datei verblieben sind.

💡 Zusätzliche Tipps
* Nutze /add [Pfad] immer für die spezifische Gradle-Datei und die libs.versions.toml, bevor du Änderungen an den Abhängigkeiten vornimmst.
* Wenn Aider Dateien im falschen Verzeichnis erstellt, korrigiere ihn sofort mit /undo.