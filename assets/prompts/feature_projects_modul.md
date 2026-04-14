Aider Prompt: Implementierung des :feature:projects Moduls
Ich möchte ein neues Android-Submodul namens :feature:projects erstellen und die Logik aus den bereitgestellten Quelldateien implementieren. Bitte folge diesen Schritten präzise:
1. Modul-Infrastruktur anlegen
* Erstelle das Verzeichnis feature/projects.
* Füge include(":feature:projects") zur settings.gradle.kts hinzu.
* Erstelle eine feature/projects/build.gradle.kts. Nutze dafür moderne Version-Catalog-Referenzen (libs.plugins..., libs.androidx...), falls im Projekt vorhanden.
* Das Modul benötigt Abhängigkeiten für:
   * Compose (UI, Material3, Tooling)
   * Navigation Compose
   * Coroutines
   * (Falls vorhanden) Projektabhängigkeiten wie :core oder :ui-common.
2. Code-Migration & Refactoring
* Implementiere die folgenden Dateien im Zielverzeichnis feature/projects/src/main/java/com/scto/mcs/feature/projects/ui/:
   * NewProjectScreen.kt
   * ProjectConfigScreen.kt
   * ProjectListScreen.kt
   * ProjectTemplates.kt
   * WorkspaceSelectionScreen.kt
* WICHTIG: Ändere das Package-Naming in allen Dateien von com.web.webide.ui.projects (oder ähnlichen) zu com.scto.mcs.feature.projects.
3. Integration & Abhängigkeitsprüfung
* Die Quelldateien referenzieren externe Komponenten. Bitte prüfe die bestehende Projektstruktur und korrigiere die Imports für:
   * WorkspaceManager, PermissionManager, LogConfigRepository -> Suche diese in :core oder entsprechenden Utilities.
   * ColorPickerDialog, DirectorySelector -> Suche diese in einem UI-Modul oder erstelle Platzhalter/Stubs, falls sie fehlen.
   * EditorViewModel -> Prüfe, ob dieses ViewModel in dieses Modul verschoben oder als Abhängigkeit eingebunden werden muss.
* Ersetze navController.safeNavigate durch navController.navigate, falls die Extension-Funktion safeNavigate in diesem Projekt nicht definiert ist.
4. Bereinigung
* Entferne die ursprünglichen Lizenz-Header der WebIDE und ersetze sie (falls im Projekt üblich) durch die Standard-Header von com.scto.mcs.
* Stelle sicher, dass alle Ressourcen-Strings (z.B. "立即创建") idealerweise in eine strings.xml im neuen Modul extrahiert werden (Deutsch/Englisch bevorzugt).
Bitte bestätige die Erstellung des Moduls und führe die Änderungen durch.