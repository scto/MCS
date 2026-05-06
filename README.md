# 📱 MCS (Mobile-Code-Studio)
**MCS** ist eine vollwertige, mobile integrierte Entwicklungsumgebung (IDE) für Android. Sie ermöglicht es Entwicklern, Android-Projekte direkt auf dem Smartphone oder Tablet zu klonen, zu bearbeiten und lokal zu kompilieren.
Das Projekt ist zu **100% in Kotlin** geschrieben und folgt den Prinzipien der **Clean Architecture** in einer hochgradig skalierbaren Multi-Modul-Struktur.
## ✨ Features
 * 🐙 **Projektverwaltung:** Repositories via Git (JGit) klonen, verwalten und direkt öffnen.
 * 📝 **Erweiterter Code-Editor:** Leistungsstarker Editor basierend auf dem *Sora-Editor* mit TextMate-Grammatiken für präzises Syntax-Highlighting. (Zukünftige LSP-Unterstützung in Planung).
 * 🖥️ **Integriertes Terminal & Build-System:** Simuliertes Terminal-Panel am unteren Bildschirmrand. Führe echte Build-Prozesse (z.B. ./gradlew assembleDebug) lokal auf dem Gerät aus.
 * 🎨 **Modernes UI:** Komplett in **Jetpack Compose** (Material 3) geschrieben. Beinhaltet einen dedizierten IDE Dark-Mode (#1E1E1E Hintergrund, Deep Blue Akzente).
 * 📦 **Lokales Environment:** Verwaltung von JDKs und Android SDKs direkt im geschützten App-Speicher (TerminalEnvironment).
## 🛠 Tech Stack
| Kategorie | Technologie | Version |
|---|---|---|
| **Sprache** | Kotlin | 2.2.0 |
| **Build-System** | Gradle (Kotlin DSL) | 8.11.2 |
| **SDK Versionen** | Min API 26 / Target 35 / Compile 36 | - |
| **UI Framework** | Jetpack Compose | BOM (Latest) |
| **Dependency Injection** | Dagger-Hilt | 2.51 |
| **Versionskontrolle** | JGit | 6.8.0 |
| **Asynchronität** | Coroutines & Flow | - |
## 📂 Architektur & Modul-Struktur
Das Projekt ist streng modular aufgebaut. Jegliche Kommunikation zwischen Daten- und UI-Schicht verläuft strikt über die Domain-Schicht (:core:domain).
```text
📦 mcs
 ┣ 📂 :app             # Application-Klasse & Hilt-Root-Komponente
 ┣ 📂 :core            # Die grundlegenden Bausteine
 ┃  ┣ 📂 :common       # Hilfsfunktionen und Basisklassen
 ┃  ┣ 📂 :utils        # Utilities
 ┃  ┣ 📂 :data         # Data Layer (Repositories, APIs)
 ┃  ┣ 📂 :domain       # Domain Layer (Use Cases, Models)
 ┃  ┣ 📂 :editor       # Zentrale Manager für Editor-Logik
 ┃  ┣ 📂 :terminal     # Terminal-Environment & Proot
 ┃  ┣ 📂 :navigation   # Typsicheres Compose-Routing
 ┃  ┣ 📂 :ui           # Material 3 Design System (Themes, Components)
 ┃  ┗ 📂 :resources    # Zentrale Strings, Icons & Drawables
 ┗ 📂 :feature         # Gekapselte Bildschirme und App-Funktionen
    ┣ 📂 :onboarding   # Berechtigungen & SDK/JDK-Downloads
    ┣ 📂 :dashboard    # Startbildschirm und Git-Clone-Dialog
    ┣ 📂 :editor       # Das Herzstück: Code-Editor und Build-Output
    ┣ 📂 :settings     # App-Konfiguration
    ┗ 📂 :debug        # Logcat-Viewer & Debugging

```
## 🚀 Getting Started
### Voraussetzungen
 * **Android Studio** (aktuellste Version empfohlen)
 * **JDK 17**
### Installation & Build
 1. **Klone das Repository:**
   ```bash
   git clone [https://github.com/DEIN_USERNAME/mcs.git](https://github.com/DEIN_USERNAME/mcs.git)
   
   ```
 2. **Öffne das Projekt** in Android Studio.
 3. Warte, bis der **Gradle-Sync** erfolgreich abgeschlossen ist.
 4. **Führe die App aus** auf einem Emulator oder einem physischen Gerät (Min. Android 8.0 / API 26).
## 📜 Coding Standards
Wir legen großen Wert auf eine saubere, wartbare Code-Basis. Bitte beachte unsere Richtlinien:
 * 💉 **Dependency Injection:** Jede Klasse muss via Dagger-Hilt injiziert werden (@Inject constructor()). Wir verwenden *keine* manuellen Service-Locators!
 * 🎨 **UI:** Ausschließlich **Jetpack Compose**. Keine traditionellen XML-Layouts (außer für Basis-Ressourcen wie strings.xml oder colors.xml).
 * 📘 **Weitere Details** findest du im coding_standards.md Dokument.
*Made with ❤️ and Kotlin.*
