# Aider Refactoring-Plan: Granulare Migration von :feature:settings/xed
Dieses Dokument enthält aufeinanderfolgende Prompts, um den Source Code aus dem xed-Verzeichnis in :feature:settings Sub-Ordner für Sub-Ordner sicher eine Ebene nach oben zu verschieben und die Imports anzupassen.
**Anweisung für den Entwickler:** Kopiere die folgenden Blöcke **einzeln** in den Aider-Chat und warte auf die Bestätigung von Aider, bevor du den nächsten Block sendest.
### Schritt 1: Kontext laden & Analyse
**Kopiere dies in Aider:**
```text
/add MCS_20260506_143519.md
Bitte analysiere die Datei `MCS_20260506_143519.md` (den Project Tree), um die genaue Paket- und Modulstruktur von MCS zu verstehen, speziell im Bereich `feature/settings`. 
Finde heraus, unter welchem genauen Pfad das Verzeichnis `feature/settings/xed` bzw. der zugehörige Code im Projektbaum liegt und welche Sub-Ordner (wie about, app, editor etc.) existieren. 
Gib mir eine kurze Liste der Struktur zurück. Nimm noch keine Code-Änderungen vor.

```
### Schritt 2: Migration Sub-Ordner about
**Kopiere dies in Aider:**
```text
/add feature/settings/src/main/java/com/scto/mcs/feature/settings/xed/about/**/*.kt

Deine Aufgabe ist es, den `about` Ordner aus dem `xed`-Verzeichnis zu migrieren:
1. Verschiebe alle Dateien von `.../settings/xed/about/` nach `.../settings/about/`.
2. Ändere in den verschobenen Dateien die `package`-Deklaration: Entferne das `.xed` (es muss `package com.scto.mcs.feature.settings.about` lauten).
3. Passe interne Imports an, falls nötig, und behalte die Funktionalität bei.

```
### Schritt 3: Migration Sub-Ordner app
**Kopiere dies in Aider:**
```text
/add feature/settings/src/main/java/com/scto/mcs/feature/settings/xed/app/**/*.kt

Deine Aufgabe ist es, den `app` Ordner aus dem `xed`-Verzeichnis zu migrieren:
1. Verschiebe alle Dateien von `.../settings/xed/app/` nach `.../settings/app/`.
2. Ändere die `package`-Deklaration auf `package com.scto.mcs.feature.settings.app`.
3. Passe interne Imports an und behalte die Funktionalität bei.

```
### Schritt 4: Migration Sub-Ordner editor
**Kopiere dies in Aider:**
```text
/add feature/settings/src/main/java/com/scto/mcs/feature/settings/xed/editor/**/*.kt

Deine Aufgabe ist es, den `editor` Ordner aus dem `xed`-Verzeichnis zu migrieren:
1. Verschiebe alle Dateien von `.../settings/xed/editor/` nach `.../settings/editor/`.
2. Ändere die `package`-Deklaration auf `package com.scto.mcs.feature.settings.editor`.
3. Passe interne Imports an und behalte die Funktionalität bei.

```
### Schritt 5: Migration Sub-Ordner terminal
**Kopiere dies in Aider:**
```text
/add feature/settings/src/main/java/com/scto/mcs/feature/settings/xed/terminal/**/*.kt

Deine Aufgabe ist es, den `terminal` Ordner aus dem `xed`-Verzeichnis zu migrieren:
1. Verschiebe alle Dateien von `.../settings/xed/terminal/` nach `.../settings/terminal/`.
2. Ändere die `package`-Deklaration auf `package com.scto.mcs.feature.settings.terminal`.
3. Passe interne Imports an und behalte die Funktionalität bei.

```
### Schritt 6: Migration weitere Sub-Ordner (git, theme, extension)
**Kopiere dies in Aider:**
```text
/add feature/settings/src/main/java/com/scto/mcs/feature/settings/xed/git/**/*.kt
/add feature/settings/src/main/java/com/scto/mcs/feature/settings/xed/theme/**/*.kt
/add feature/settings/src/main/java/com/scto/mcs/feature/settings/xed/extension/**/*.kt

Deine Aufgabe ist es, die Ordner `git`, `theme` und `extension` aus dem `xed`-Verzeichnis zu migrieren:
1. Verschiebe die Dateien aus `xed/<ordner>/` nach `<ordner>/` (eine Ebene nach oben).
2. Ändere in allen Dateien die `package`-Deklaration (entferne `.xed`).
3. Passe interne Imports an und behalte die Funktionalität bei.

```
### Schritt 7: Migration verbleibende Sub-Ordner (keybinds, language, lsp, runners, support, debugOptions)
**Kopiere dies in Aider:**
```text
/add feature/settings/src/main/java/com/scto/mcs/feature/settings/xed/keybinds/**/*.kt
/add feature/settings/src/main/java/com/scto/mcs/feature/settings/xed/language/**/*.kt
/add feature/settings/src/main/java/com/scto/mcs/feature/settings/xed/lsp/**/*.kt
/add feature/settings/src/main/java/com/scto/mcs/feature/settings/xed/runners/**/*.kt
/add feature/settings/src/main/java/com/scto/mcs/feature/settings/xed/support/**/*.kt
/add feature/settings/src/main/java/com/scto/mcs/feature/settings/xed/debugOptions/**/*.kt
/add feature/settings/src/main/java/com/scto/mcs/feature/settings/xed/debug/**/*.kt

Deine Aufgabe ist es, diese restlichen Sub-Ordner aus `xed` eine Ebene nach oben zu verschieben:
1. Verschiebe die Dateien aus `xed/<ordner>/` nach `<ordner>/`.
2. Ändere die `package`-Deklarationen (entferne `.xed`).
3. Passe interne Imports an und behalte die Funktionalität bei.

```
### Schritt 8: Root-Dateien von feature/settings/xed migrieren
**Kopiere dies in Aider:**
```text
/add feature/settings/src/main/java/com/scto/mcs/feature/settings/xed/*.kt

Deine Aufgabe ist es, alle direkt im `xed`-Ordner verbliebenen Dateien (z.B. Konstanten, Haupt-Screens, Routen) zu migrieren:
1. Verschiebe diese Dateien nach `feature/settings/src/main/java/com/scto/mcs/feature/settings/`.
2. Aktualisiere die `package`-Deklarationen auf `package com.scto.mcs.feature.settings`.
3. Passe interne Imports an.

```
### Schritt 9: Lokale und Globale Import-Korrekturen
**Kopiere dies in Aider:**
```text
/add feature/settings/src/main/java/com/scto/mcs/feature/settings/**/*.kt
/add app/src/main/java/com/scto/mcs/app/**/*.kt

Wir haben nun alle Dateien verschoben. Bitte überprüfe das Settings-Modul und das App-Modul auf fehlerhafte Imports:
1. Suche nach jeglichen Imports, die noch auf `.xed.` verweisen (z.B. `import com.scto.mcs.feature.settings.xed.AppLogs`).
2. Entferne das `.xed` aus diesen Import-Pfaden.
3. Lösche redundante Imports, die durch das Zusammenführen von Verzeichnissen obsolet geworden sind.

```
### Schritt 10: Aufräumen (Cleanup)
**Kopiere dies in Aider:**
```text
/run rm -rf feature/settings/src/main/java/com/scto/mcs/feature/settings/xed

Bitte bestätige, dass das nun leere `xed`-Verzeichnis erfolgreich gelöscht wurde. Führe einen finalen visuellen Code-Check durch, ob der Build-Prozess bezüglich der Settings-Imports intakt sein sollte.

```
