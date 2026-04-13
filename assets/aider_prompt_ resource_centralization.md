Aider Prompt: Ressourcen-Zentralisierung in :core:resources
Handle als Senior Android Architect. Ziel ist es, alle Ressourcen aus allen Modulen und Submodulen in das zentrale Modul :core:resources zu verschieben und die Quellordner zu bereinigen.
1. Ziel-Modul (Destination)
* Pfad: core/resources/src/main/res/
* Das Paket der zentralen R-Klasse muss identifiziert werden (aus der AndroidManifest.xml oder build.gradle.kts von :core:resources).
2. Quell-Module (Source)
* Alle Module und Submodule außer :core:resources.
3. Aufgabenstellung
Phase 1: Ressourcen-Migration
1. Iteriere durch alle Module. Identifiziere src/main/res Ordner.
2. Verschiebe alle Inhalte (Drawables, Layouts, Values, etc.) nach core/resources/src/main/res/.
3. Konfliktlösung bei XML-Dateien:
   * Falls eine Datei wie strings.xml bereits im Ziel existiert, führe die <resources>-Einträge zusammen (merging), anstatt die Datei zu überschreiben.
   * Falls Dateinamen (z. B. fragment_main.xml) kollidieren, benenne sie um, indem du das Ursprungsmodul als Prefix nutzt, und vermerke dies für das spätere Refactoring im Code.
Phase 2: Refactoring der Kotlin-Dateien (WICHTIG)
Da die Ressourcen nun in :core:resources liegen, müssen alle Importe im Projekt angepasst werden:
1. Suche in allen .kt Dateien nach Importen der lokalen R-Klasse (z. B. import com.example.feature.R).
2. Ersetze diese durch den Import der zentralen R-Klasse (z. B. import com.example.core.resources.R).
3. Stelle sicher, dass auch vollqualifizierte Aufrufe im Code (falls vorhanden) angepasst werden.
Phase 3: Bereinigung
1. Sobald alle Ressourcen erfolgreich verschoben und die Code-Referenzen aktualisiert wurden, lösche die nun leeren src/main/res Verzeichnisse in den Quell-Modulen.
2. Entferne keine Dateien innerhalb von :core:resources.
4. Sicherheitsregeln
* Lösche einen Quell-Ordner erst, wenn sichergestellt ist, dass die Inhalte im Ziel-Modul vorhanden sind.
* Verändere keine Business-Logik, nur Importe und Ressourcen-Dateien.
* Gib eine Liste der verschobenen Ressourcen und der bereinigten Module aus.
Starte jetzt mit der Analyse der Modulstruktur und beginne die Migration.