MCS (Mobile-Code-Studio) 📱💻
MCS ist eine vollwertige, mobile integrierte Entwicklungsumgebung (IDE) für Android. Sie ermöglicht es Entwicklern, Android-Projekte direkt auf dem Smartphone oder Tablet zu klonen, zu bearbeiten und lokal zu kompilieren.
Das Projekt ist zu 100% in Kotlin geschrieben und folgt den Prinzipien der Clean Architecture in einer skalierbaren Multi-Modul-Struktur.
✨ Features
* Projektverwaltung: Repositories via Git (JGit) klonen, verwalten und öffnen.
* Erweiterter Code-Editor: Leistungsstarker Editor basierend auf dem Sora-Editor mit TextMate-Grammatiken für präzises Syntax-Highlighting und zukünftiger LSP-Unterstützung.
* Integriertes Terminal & Build-System: Simuliertes Terminal-Panel am unteren Bildschirmrand. Führe echte Build-Prozesse (./gradlew assembleDebug) lokal auf dem Gerät aus.
* Modernes UI: Komplett in Jetpack Compose (Material 3) geschrieben, mit einem dedizierten IDE Dark-Mode (#1E1E1E Hintergrund, Deep Blue Akzente).
* Lokales Environment: Verwaltung von JDKs und Android SDKs direkt im geschützten App-Speicher (TerminalEnvironment).
🛠 Tech Stack
* Sprache: Kotlin 2.2.0
* Build-System: Gradle 8.11.2 (Kotlin DSL)
* SDK Versionen: Min 26, Target 35, Compile 36
* UI Framework: Jetpack Compose (BOM)
* Dependency Injection: Dagger-Hilt (2.51)
* Versionskontrolle: JGit (6.8.0)
* Asynchronität: Kotlin Coroutines & Flow
📂 Architektur & Modul-Struktur
Das Projekt ist streng modular aufgebaut. Jegliche Kommunikation zwischen Daten- und UI-Schicht verläuft über die Domain-Schicht (:core:domain).
* :app – Application-Klasse & Hilt-Root-Komponente.
* :core – Die grundlegenden Bausteine:
   * :common, :utils – Hilfsfunktionen und Basisklassen.
   * :data, :domain – Clean Architecture Schichten (Repositories, Use Cases).
   * :editor, :terminal – Zentrale Manager für Editor-Logik und Terminal-Environment.
   * :navigation – Typsicheres Compose-Routing.
   * :ui, :resourcess – Das Material 3 Design System (Themes, Icons).
* :feature – Gekapselte Bildschirme und App-Funktionen:
   * :onboarding, :setup – Berechtigungen und SDK/JDK-Downloads.
   * :dashboard – Startbildschirm und Git-Clone-Dialog.
   * :editor – Das Herzstück: Code-Editor und Build-Output.
   * :settings, :debug – App-Konfiguration und Logcat-Viewer.
🚀 Getting Started
Voraussetzungen
* Android Studio (aktuellste Version empfohlen)
* JDK 17
Installation
1. Klone das Repository:
git clone [https://github.com/DEIN_USERNAME/mcs.git](https://github.com/DEIN_USERNAME/mcs.git)

2. Öffne das Projekt in Android Studio.
3. Warte, bis der Gradle-Sync abgeschlossen ist.
4. Führe die App auf einem Emulator oder physischen Gerät (Min. Android 8.0 / API 26) aus.
📜 Coding Standards
Bitte beachte unsere Richtlinien für die Code-Qualität:
   * Dependency Injection: Jede Klasse muss via Dagger-Hilt injiziert werden (@Inject constructor()). Keine manuellen Service-Locators!
   * UI: Ausschließlich Jetpack Compose. Keine XML-Layouts (außer für Basis-Ressourcen wie strings.xml oder colors.xml).
   * Weitere Details im coding_standards.md Dokument.