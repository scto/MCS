# Aider Refactoring-Plan: Migration von :core:settings/xed
Dieses Dokument enthält aufeinanderfolgende Prompts, um den Source Code aus dem xed-Verzeichnis eine Ebene nach oben zu verschieben und die Imports sicher anzupassen.
**Anweisung für den Entwickler:** Kopiere die folgenden Blöcke **einzeln** in den Aider-Chat und warte auf die Bestätigung, bevor du den nächsten Block sendest.
### Schritt 1: Kontext laden & Analyse
**Kopiere dies in Aider:**
```text
/add MCS_20260502_125638.md
Bitte analysiere die Datei `MCS_20260502_125638.md` (den Project Tree), um die genaue Paket- und Modulstruktur von MCS zu verstehen. 
Finde heraus, unter welchem genauen Pfad das Verzeichnis `:core:settings/xed` bzw. der zugehörige Code im Projektbaum liegt. 
Gib mir eine kurze Liste der Dateien zurück, die sich in diesem `xed`-Ordner befinden. Nimm noch keine Code-Änderungen vor.

```
### Schritt 2: Verschieben und Paketnamen anpassen (Teil 1 - UI & Screens)
**Kopiere dies in Aider:**
```text
/add core/settings/src/main/java/com/scto/mcs/core/settings/xed/*Screen*.kt
/add core/settings/src/main/java/com/scto/mcs/core/settings/xed/*View*.kt

Deine Aufgabe ist es, diese UI- und Screen-Dateien aus dem `xed`-Verzeichnis eine Ebene nach oben in `core/settings` zu migrieren:
1. Benenne die Dateipfade um (verschiebe sie von `.../settings/xed/` nach `.../settings/`). Nutze dazu die Git-Move-Funktionalität.
2. Ändere in allen verschobenen Dateien die `package`-Deklaration, sodass das `.xed` am Ende verschwindet (z.B. `package com.scto.mcs.core.settings`).
3. Behalte die gesamte Funktionalität exakt bei.

```
### Schritt 3: Verschieben und Paketnamen anpassen (Teil 2 - Logik, ViewModels & Utils)
**Kopiere dies in Aider:**
```text
/add core/settings/src/main/java/com/scto/mcs/core/settings/xed/*.kt

Deine Aufgabe ist es, alle verbleibenden Dateien (ViewModels, Utils, Konstanten etc.) aus dem `xed`-Verzeichnis eine Ebene nach oben zu migrieren:
1. Verschiebe alle verbleibenden `.kt` Dateien aus `.../settings/xed/` nach `.../settings/`.
2. Aktualisiere in diesen Dateien die `package`-Deklaration (entferne das `.xed`).
3. Behalte die gesamte Funktionalität bei.

```
### Schritt 4: Lokale Import-Korrekturen (Innerhalb von core:settings)
**Kopiere dies in Aider:**
```text
/add core/settings/src/main/java/com/scto/mcs/core/settings/**/*.kt

Wir haben nun alle Dateien verschoben. Bitte überprüfe alle Dateien im `:core:settings` Modul auf fehlerhafte Imports:
1. Suche nach Imports, die noch `.xed.` enthalten (z.B. `import com.scto.mcs.core.settings.xed.MyClass`).
2. Entferne das `.xed` aus diesen Import-Pfaden.
3. Kontrolliere, ob durch das Verschieben in dasselbe Verzeichnis einige Imports komplett überflüssig geworden sind, und entferne diese.

```
### Schritt 5: Globale Import-Korrekturen & Referenz-Updates
**Kopiere dies in Aider:**
```text
/add app/src/main/java/com/scto/mcs/app/**/*.kt
/add feature/**/*.kt

Da die Klassen aus `core:settings:xed` nun in `core:settings` liegen, müssen andere Module angepasst werden.
1. Suche in den geladenen Dateien des `:app`-Moduls und der `:feature`-Module nach alten Imports, die auf `com.scto.mcs.core.settings.xed` verweisen.
2. Ersetze diese durch den neuen Pfad `com.scto.mcs.core.settings`.
3. Nutze das Wissen aus der `MCS_20260502_125638.md`, um sicherzustellen, dass die Imports perfekt zur neuen Modulstruktur passen.

```
### Schritt 6: Aufräumen (Cleanup)
**Kopiere dies in Aider:**
```text
/run rm -rf core/settings/src/main/java/com/scto/mcs/core/settings/xed

Bitte bestätige, dass das leere `xed`-Verzeichnis erfolgreich gelöscht wurde und führe einen finalen Check durch, ob der Build-Prozess logisch intakt sein sollte.

```
