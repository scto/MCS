Aider Prompt: Settings Module Implementation
Nutze Jetpack Compose und Kotlin, um das Settings-Modul im Pfad feature/settings zu implementieren. Erstelle einen SettingsScreen und ein zugehöriges SettingsViewModel.
Architektur & Dateistruktur
* Zielverzeichnis: feature/settings/
* Dateien:
   * SettingsScreen.kt: Enthält die Compose-UI (Hauptmenü und Unterseiten).
   * SettingsViewModel.kt: Verwaltet den Zustand der Einstellungen (StateFlow).
   * SettingsState.kt: Datenklasse für die Einstellungs-Werte.
Anforderungen basierend auf den Screenshots
1. Haupt-Settings-Screen (Navigation)
* Implementiere eine TopAppBar mit "Settings" und einem Back-Button.
* Erstelle zwei Sektionen: "Configure" und "About".
* Configure: General, Editor, File Explorer, Plugins, Login with GitHub.
* About: GitHub (Visual Code Space is open source!), Open Source Licences.
* Jedes Item soll einen Titel und eine graue Sub-Beschreibung haben.
2. Unterseite: General (Screenshot 1003643842)
* Sektion "General" mit folgenden Schaltern (Switches):
   * Follow System Theme: Mit Palette-Icon.
   * Use Dark Mode: Mit Zahnrad/Sonne-Icon.
   * Use Amoled Mode: Mit Kontrast-Icon.
   * Dynamic Colors: Mit Palette-Icon.
   * Enable gesture in drawer: Mit Kurven-Icon.
3. Unterseite: Editor (Screenshot 1003643844)
* Sektion "Editor": "Current Editor (Sora)", "Typing Tip", "Show Input Method Picker at Start" (Switch).
* Sektion "Editor Settings":
   * Font Size: Slider (Bereich 8-30 sp, Default 14 sp).
   * Indent Size: Text-Anzeige (z.B. 4 spaces).
   * Font Family, Color Scheme, Symbols.
   * Switches für: Font Ligatures, Sticky Scroll, Word Wrap, Show Line Numbers, Use Tabs, Delete Line on Backspace, Delete Indent on Backspace.
* Sektion "Tabs".
4. Unterseite: File Settings (Screenshot 1003643845)
* Sektion "File Settings".
* Switch: Show Hidden Files (Hidden files are not displayed).
5. Unterseite: Plugins (Screenshot 1003643846)
* Eigene Seite mit Tab "Installed".
* "No plugins found" Text in der Mitte.
* Floating Action Button (FAB) unten rechts mit "+ New Plugin".
Technische Details
* Verwende Material 3 Komponenten.
* Das Design soll "Dark Theme" sein (Hintergrund: Schwarz/Dunkelviolett-Stich, Akzente: Hellviolett/Lila).
* Nutze Scaffold für die Struktur.
* Das ViewModel soll die Zustände (Boolean für Switches, Int für Slider) via MutableStateFlow halten und persistierbar vorbereiten (erstmal nur In-Memory, aber modular).
* Nutze eine einfache Navigation innerhalb des Screens (z.B. via Crossfade oder ein lokales NavHost), um zwischen Hauptmenü und Unterseiten zu wechseln.
Bitte erstelle den vollständigen Kotlin-Code für diese Komponenten.