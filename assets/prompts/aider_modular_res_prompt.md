Aider Prompt: Deep Scan, Resource Generation & String Extraction
Handle als Senior Android Architect. Deine Aufgabe ist die systematische Erstellung von Ressourcen und das Refactoring von hartkodierten Texten in einem Kotlin-Projekt.
1. Exklusiver Scope (Modul-Fokus)
Bearbeite ausschließlich:
* :app (Verzeichnis /app)
* :core:${submodule} (Verzeichnisse unter /core/)
* :feature:${submodule} (Verzeichnisse unter /feature/)
2. Infrastruktur & Fehlende Ressourcen
1. Verzeichnisse: Stelle sicher, dass src/main/res/values in jedem Modul existiert.
2. Fehlende R-Referenzen: Scanne .kt Dateien nach R.string.*, stringResource(R.string.*) etc. Falls diese in der strings.xml fehlen, erstelle sie.
3. NEU: Extraktion hartkodierter Strings
Scanne alle Kotlin-Dateien (.kt) nach hartkodierten Strings, die an UI-Komponenten übergeben werden (z.B. Text("Hallo"), Button(text = "Klick mich"), setTitle("Mein Titel")).
Regeln für die Extraktion:
1. Identifikation: Extrahiere nur benutzerrelevante Texte (keine Logs, keine Keys für Datenbanken/APIs).
2. Verschiebung: Verschiebe den Text in die strings.xml des jeweiligen Moduls.
3. Ersetzung im Code:
   * In Compose: Ersetze "Text" durch stringResource(id = R.string.prefix_name).
   * In Standard Kotlin: Ersetze "Text" durch context.getString(R.string.prefix_name) oder getString(R.string.prefix_name), je nach Kontext.
4. Naming Convention:
   * Nutze das Submodul-Präfix (z.B. login_welcome_message).
   * Erstelle sprechende Namen basierend auf dem Inhalt oder der Variablenbezeichnung.
4. Generierungsvorgaben
* Strings: <string name="prefix_name">Inhalt des hartkodierten Strings</string>.
* Dateiformat: snake_case für IDs, korrekte XML-Struktur.
* Vermeide Duplikate: Wenn ein identischer Text bereits in der strings.xml existiert, verwende die vorhandene ID.
5. Durchführung
1. Analysiere erst die Module in :core, dann :feature, dann :app.
2. Führe die Extraktion und Ersetzung pro Modul gesammelt durch.
3. Gib eine Zusammenfassung der extrahierten Strings pro Modul aus.
Starte jetzt mit dem Scan und Refactoring.
### So führst du es für das Feature-Paket aus:

In deiner aktiven Aider-Sitzung:

1.  **Dateien laden (falls noch nicht geschehen):**
   ```lisp
   /drop
   /read aider_refactor_res_prompt.md
   /add feature/*/src/main/java/**/*.kt
   /add feature/*/src/main/res/**/*.xml