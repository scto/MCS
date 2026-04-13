Aider Prompt: Ressourcen-Zentralisierung in :core:resourcess
Handle als Senior Android Architect. Dein Ziel ist die Konsolidierung aller Ressourcen in das zentrale Modul :core:resourcess und die Bereinigung der restlichen Projektstruktur.
1. Ziel-Konfiguration
* Ziel-Modul: :core:resourcess (Pfad: core/resourcess/)
* Ziel-R-Klasse: Identifiziere den Namespace/das Package in core/resourcess/src/main/AndroidManifest.xml oder core/resourcess/build.gradle.kts.
2. Aufgaben
Phase 1: Migration der Ressourcen
1. Identifiziere alle src/main/res/-Ordner in allen Modulen (außer in :core:resourcess).
2. Verschiebe alle Inhalte (layouts, drawables, values, xml, etc.) nach core/resourcess/src/main/res/.
3. Merging: Falls XML-Dateien wie strings.xml oder colors.xml bereits im Ziel existieren, führe die <resources>-Einträge zusammen, anstatt die Datei zu überschreiben.
4. Konfliktmanagement: Falls Dateinamen (z. B. activity_main.xml) kollidieren, benenne sie um (z. B. feature_login_activity_main.xml) und passe alle Referenzen im Code entsprechend an.
Phase 2: Refactoring der Kotlin-Dateien
1. Suche in allen .kt Dateien (Compose & XML-basiert) nach Importen der lokalen R-Klassen der Quell-Module.
2. Ersetze diese durch den Import der zentralen R-Klasse aus :core:resourcess.
3. Korrigiere auch vollqualifizierte Aufrufe (z.B. com.example.feature.R.string... -> com.example.core.resourcess.R.string...).
Phase 3: Gradle-Konfiguration
1. Füge in allen build.gradle.kts Dateien der Module, aus denen Ressourcen entfernt wurden, die Abhängigkeit implementation(project(":core:resourcess")) hinzu.
2. Stelle sicher, dass das Modul :core:resourcess die Ressourcen so bereitstellt, dass sie für die anderen Module sichtbar sind.
Phase 4: Bereinigung
1. Lösche die nun leeren src/main/res-Ordner in den Quell-Modulen erst, nachdem die Migration und das Refactoring erfolgreich abgeschlossen wurden.
3. Regeln
* Keine Änderungen an der Business-Logik.
* Beachte die Kotlin-Best-Practices.
* Gib eine Zusammenfassung der verschobenen Dateien und aktualisierten Module aus.
Starte jetzt mit der Analyse von :core:resourcess und beginne die Migration.