Aider Prompt: Implementierung des :feature:settings Moduls
Ich möchte das Android-Submodul namens :feature:settings überarbeiten und die Logik aus den bereitgestellten Quelldateien implementieren. Bitte folge diesen Schritten präzise:
1. Modul-Infrastruktur anlegen
* Erstelle das Verzeichnis feature/projects falls noch nicht vorhanden.
* Füge falls noch nicht vorhanden include(":feature:settings") zur settings.gradle.kts hinzu.
* Erstelle falls noch nicht vorhanden eine feature/settings/build.gradle.kts. Nutze dafür moderne Version-Catalog-Referenzen (libs.plugins..., libs.androidx...), falls im Projekt vorhanden.
* Das Modul benötigt Abhängigkeiten für:
   * Compose (UI, Material3, Tooling)
   * Navigation Compose
   * Coroutines
   * (Falls vorhanden) Projektabhängigkeiten wie :core oder :ui-common.
2. Code-Migration & Refactoring
* Implementiere die folgenden Dateien im Zielverzeichnis feature/settings/src/main/java/com/scto/mcs/feature/settings/ui/:
   * AboutScreen.kt
   * SettingsScreen.kt
* WICHTIG: Ändere das Package-Naming in allen Dateien von com.web.webide.ui.settings (oder ähnlichen) zu com.scto.mcs.feature.settings.
* ACHTUNG: Ändere auf keinen Fall die Root build.gradle.kts
3. Integration & Abhängigkeitsprüfung
* Die Quelldateien referenzieren externe Komponenten. Bitte prüfe die bestehende Projektstruktur und korrigiere die Imports für:
   * AboutScreen, , 
   * SettingsScreen, erstelle Platzhalter/Stubs, falls sie fehlen.
   * SettingsViewModel -> Prüfe, ob dieses ViewModel in dieses Modul verschoben oder als Abhängigkeit eingebunden werden muss.
* Ersetze navController.safeNavigate durch navController.navigate, falls die Extension-Funktion safeNavigate in diesem Projekt nicht definiert ist.
4. Bereinigung
* Entferne die ursprünglichen Lizenz-Header der WebIDE und ersetze sie (falls im Projekt üblich) durch die Standard-Header von com.scto.mcs.
* Stelle sicher, dass alle Ressourcen-Strings (z.B. "设置") idealerweise in eine strings.xml im neuen Modul extrahiert werden (Deutsch/Englisch bevorzugt).
Bitte bestätige die Erstellung des Moduls und führe die Änderungen durch.