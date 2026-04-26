Aider Ausführungsanleitung: Refactoring & Zentralisierung
Diese Anleitung beschreibt die exakten Terminal-Befehle und Chat-Prompts für die Durchführung der Schritte 6.1 und 6.2.
Schritt 6.1: Ressourcen-Verschiebung (Files)
Terminal-Befehl: aider **/*.xml
Chat-Prompt:
Führe Schritt 6.1 aus dem Refactor-Plan aus:
1. Verschiebe alle Ressourcen (drawables, layouts, values, xml, etc.) aus allen App-, Feature- und Core-Modulen physisch in das Modul :core:resources.
2. Der Zielpfad für alle Dateien ist core/resources/src/main/res/.
3. Achte darauf, die Verzeichnisstruktur (z.B. drawable-anydpi, values-de) beizubehalten.
4. Lösche die Dateien an den Ursprungsorten erst, wenn sie am Zielort existieren.
Schritt 6.2: String-Management & Code-Anpassung
Terminal-Befehl: aider core/resources/src/main/res/values/*.xml **/*.kt **/*.java
Chat-Prompt:
Führe Schritt 6.2 aus dem Refactor-Plan aus:
1. Führe alle Inhalte der verschiedenen strings.xml Dateien in der zentralen Datei core/resources/src/main/res/values/strings.xml zusammen. Eliminiere dabei doppelte Keys.
2. Durchsuche alle Kotlin- und Java-Dateien nach hartkodierten UI-Strings und ersetze diese durch entsprechende R.string-Referenzen. Erstelle neue Keys in der strings.xml, falls nötig.
3. Aktualisiere alle Ressourcen-Imports im gesamten Projekt auf com.scto.mcs.core.resources.R.
4. Lösche alle nun leeren res-Ordner in den ursprünglichen Modulen.
5. Stelle sicher, dass keine alten mcs- oder msc-Überschneidungen in den Imports verbleiben.
Tipps zur Durchführung von Schritt 6
* Schritt 6.1 konzentriert sich rein auf die Dateistruktur (Filesystem).
* Schritt 6.2 konzentriert sich auf die Logik (Inhalt der Dateien und Imports).
* Es wird dringend empfohlen, nach Abschluss von 6.1 einen Git-Commit durchzuführen, bevor 6.2 gestartet wird.
* Bei sehr vielen Dateien in 6.2 kann es helfen, aider anzuweisen: "Verarbeite die Imports modulweise, um den Kontext nicht zu sprengen."