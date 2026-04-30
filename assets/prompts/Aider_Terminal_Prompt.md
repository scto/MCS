/add core/terminal//*.kt feature/terminal//.kt core/files/src/main/java/com/scto/mcs/core/files/repository/.kt core/di//*.kt core/domain//.kt core/editor/src/main/java/com/scto/mcs/core/editor/tabs/**/.kt
Führe eine tiefgreifende Integration von :core:terminalxed in :core:terminal durch. Implementiere die moderne Architektur unter Einbeziehung von DI, Domain und Files.

1. Erweiterung :core:files (Repository):
   * Aktualisiere FileRepository und FileRepositoryImpl. Implementiere:
      * saveInternalScript(name: String, content: String): Speichert Shell-Skripte im privaten App-Bin-Ordner.
      * ensureDirectoryStructure(paths: List<String>): Erstellt rekursiv Verzeichnisse für das RootFS.
      * readAsset(path: String): String: Hilfsmethode zum Laden der Shell-Templates.

2. Migration TerminalConfig:
   * Ersetze XedConstants durch TerminalConfig (:core:terminal/config).
   * Alle Pfade für proot, home, tmp und bin müssen reaktiv über die Config bezogen werden.

3. Session-Logik & Editor-Integration:
   * Der TerminalSessionManager muss den TabManager (:core:editor/tabs) injiziert bekommen.
   * Logik für getPwd(): Wenn eine neue Session erstellt wird und Settings.project_as_pwd wahr ist, frage TabManager.currentTab ab. Falls dieser ein EditorTab ist, setze das Arbeitsverzeichnis auf den Parent-Ordner der Datei.
   * Nutze den SessionService als Foreground-Komponente für die Prozess-Persistenz.

4. UI & ViewModel Modernisierung:
   * Der TerminalScreen (:feature:terminal/ui) muss das Pager-System (Virtual Keys & Input) und den Session-Drawer aus der xed-Sicherung übernehmen.
   * Das TerminalViewModel steuert den TerminalSessionManager und den TerminalSetupService.
   * Binde die native TerminalView (Termux) via AndroidView im Screen ein.

5. Virtual Keys:
   * Migriere die VirtualKeysView nach com.scto.mcs.core.terminal.virtualkeys.
   * Sorge dafür, dass TerminalBackEnd die Sondertasten-Status (Ctrl/Alt) direkt vom ViewModel bezieht (reaktive Kopplung).
6. Hilt Integration:
   * Nutze die bereitgestellten Module in :core:di, um TerminalService, FileRepository und TerminalSessionManager als Singletons zu registrieren.
7. Cleanup:
   * Nutze die globale Import-Mapping-Tabelle. Entferne alle "Xed"-Präfixe und ungenutzten Code.
Erkläre nach Abschluss kurz, wie die Kommunikation zwischen Terminal und Editor-Tabs architektonisch gelöst wurde.