Zielsetzung
Implementiere das Modul :feature:settings für die App "MCS". Nutze Kotlin und Jetpack Compose. Die UI und Struktur orientieren sich an den beigefügten Screenshots.
1. Ressourcen & Texte (strings.xml)
Erstelle die Datei src/main/res/values/strings.xml. Extrahiere alle Texte aus den Screenshots (1003710732.png und 1003716141.png) und wende folgende Ersetzungen an:
* Ersetze "Xed-editor" oder "Visual Code Space" durch MCS.
* Setze den Autor-Namen auf Thomas Schmid.
* Setze die GitHub-URL auf github.com/scto/MCS.
* Erstelle Einträge für alle Sektionen ("Configure", "About") und alle Listenelemente (General, Editor, Terminal, etc.).
2. Architektur & Komponenten
Implementiere das Feature im Submodul :feature:settings mit folgenden Komponenten:
* DI: di/SettingsModule.kt unter Verwendung von Hilt.
* ViewModel: ui/SettingsViewModel.kt zur Verwaltung der Logik.
* State: ui/SettingsState.kt (MVI-Pattern), um den Zustand der Einstellungsliste zu halten.
* UI: ui/SettingsScreen.kt basierend auf dem Design von 1003710732.png.
   * Verwende ein dunkles Theme mit violetten Akzenten.
   * Die Liste der Einstellungen soll dynamisch die Ordnerstruktur aus 1003716141.png abbilden (about, app, debugOptions, editor, extension, git, keybinds, language, lsp, runners, support, terminal, theme).
3. Navigation
* Nutze das bestehende :core:navigation.
* Erstelle navigation/SettingsRoutes.kt innerhalb des Submoduls.
* Definiere Routen für die Hauptansicht und für jeden Subordner der Liste (z.B. Terminal-Einstellungen, Editor-Einstellungen).
4. Code-Struktur
Orientiere dich bei der Verzeichnisstruktur an dem Attachment 1003716141.png. Erstelle für die verschiedenen Einstellungsbereiche entsprechende Unterordner/Packages im UI-Layer, um eine saubere Trennung zu gewährleisten.
Technische Anforderungen
* Programmiersprache: Kotlin.
* UI-Framework: Jetpack Compose mit Material 3.
* Icons: Nutze passende Material Design Icons für die jeweiligen Menüpunkte.
* Hardcoded Strings: Verboten. Nutze ausschließlich die neu erstellte strings.xml.