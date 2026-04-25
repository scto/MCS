Ziel: Vollständige Integration und Konfiguration des Terminal-Systems
Verbinde die folgenden Module und Komponenten, um ein voll funktionsfähiges, ausführbares und visuell konfigurierbares Terminal zu implementieren.
1. Modul-Verknüpfung & DI
* Verknüpfe :app und :feature:terminal mit den Core-Modulen: :core:terminal, :core:exec, :core:di, :core:files, :core:navigation, :core:network, :core:resources, :core:ui, :core:utils.
* Stelle sicher, dass das Modul :feature:settings eingebunden ist, um Terminal-Konfigurationen bereitzustellen.
* Konfiguriere Hilt in :core:di, um TerminalService, SessionCreator und FileRepository bereitzustellen.
2. Funktionale Logik (Backend-to-Session)
* Setup-Flow: Integriere den TerminalSetupService. Beim Start der TerminalActivity muss geprüft werden, ob PRoot und RootFS installiert sind. Falls nicht, starte den Download via :core:network.
* Execution: Nutze den ProotProcessWrapper aus :core:exec im TerminalService. Die Pfade müssen über das FileRepository (:core:files) bezogen werden.
* Session-Handling: Implementiere den TerminalSessionManager. Erzeuge beim Start eine neue Session via SessionCreator (inkl. korrekter Umgebungsvariablen wie PATH, LD_LIBRARY_PATH und HOME).
3. Terminal-Emulator UI & UX (Spezifische Details)
* TerminalActivity: Hostet den UI-Screen aus :feature:terminal.
* UI-Anpassung via Settings: Verbinde das SettingsViewModel aus :feature:settings mit dem Terminal-Emulator:
   * Schriftgröße: Implementiere eine dynamische Anpassung der Textgröße im Terminal (z. B. 12sp bis 18sp).
   * Cursor-Styling: Füge Unterstützung für Cursor-Blinken (An/Aus) und Cursor-Typ (Block, Unterstrich) hinzu.
   * Farbschema: Das Terminal muss die AMOLED- und Dark/Light-Einstellungen aus den Settings übernehmen. Nutze die ANSI-Farben passend zum App-Theme.
   * Scrollback: Implementiere einen konfigurierbaren Scrollback-Buffer (Anzahl der Zeilen im Verlauf).
4. Manifest & System-Integration
* Überprüfe app/src/main/AndroidManifest.xml:
   * Benötigte Permissions: INTERNET, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE.
   * Falls targetSdk >= 30: Füge MANAGE_EXTERNAL_STORAGE hinzu oder stelle sicher, dass Scoped Storage korrekt gehandhabt wird.
   * Registriere TerminalActivity mit windowSoftInputMode="adjustResize", damit die Tastatur das Terminal nicht verdeckt.
5. Coding-Standard
* Sprache: Kotlin.
* UI: Jetpack Compose (wo anwendbar).
* Kommunikation: Verwende StateFlow für den Output-Stream des Terminals und Coroutines für die Prozessverwaltung.