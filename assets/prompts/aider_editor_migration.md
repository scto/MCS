Aider Prompt: Editor Migration & Architectural Refactoring
Handle als Senior Android Architect. Deine Aufgabe ist die Migration und das Refactoring des Editor-Codes von einem Legacy-Pfad in das moderne Feature-Modul-System.
1. Source & Destination
* Quelle: assets/src/editor (Legacy-Code)
* Ziel-Paket: com.scto.mcs.feature.editor
* Ziel-Verzeichnis: feature/editor/src/main/java/com/scto/mcs/feature/editor/
* Package-Umbenennung: Ändere com.web.webide.ui.editor zu com.scto.mcs.feature.editor.
2. Integration & Kontext
Berücksichtige die Abhängigkeiten und Schnittstellen zu folgenden Modulen:
* :core:editor (Editor-Abstraktionen)
* :core:navigation (Navigation-Logik)
* :core:lsp (Language Server Protocol Integration)
* :core:utils (Common Utilities)
* :core:ui (Zentrale UI-Komponenten/Themes)
* :feature:settings (Editor-Einstellungen)
3. Aufgabenstellung
Phase 1: Migration & Package Update
1. Verschiebe alle Dateien von assets/src/editor in das Ziel-Verzeichnis.
2. Aktualisiere alle package-Deklarationen und import-Statements auf com.scto.mcs.feature.editor.
3. Behandle Ressourcen-Referenzen (R.) so, dass sie nun auf :core:resourcess (oder das lokale Modul) verweisen.
Phase 2: Refactoring & Ergänzung
1. Keine Löschung: Entferne keine bestehenden Funktionen des Legacy-Codes. Ergänze stattdessen den bereits in :feature:editor vorhandenen Code.
2. Architektur-Anpassung: - Integriere @Inject (Hilt) für Konstruktoren, wo es sinnvoll ist.
   * Nutze UseCases aus :core:domain (wie LoadFileContentUseCase), falls der Legacy-Code eigene Datei-Lade-Logik hat.
   * Nutze das LSP-Modul :core:lsp für Syntax-Highlighting oder Code-Completion, falls im Legacy-Code vorhanden.
3. Clean Code: Überarbeite den Code gemäß moderner Kotlin-Konventionen (Coroutines statt Callbacks, StateFlow/Compose-Kompatibilität).
4. Regeln
* Ersetze hartkodierte Strings durch Ressourcen-Referenzen (nutze den editor_ Prefix).
* Nutze :core:ui für konsistente Komponenten.
* Gib eine Zusammenfassung der migrierten Klassen und der vorgenommenen Integrationen aus.
Beginne jetzt mit der Analyse des Quellcodes in assets/src/editor.