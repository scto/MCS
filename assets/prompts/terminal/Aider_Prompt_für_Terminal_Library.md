Aider Architektur-Prompt: Terminal Logic Refactoring & Migration
Führe einen vollständigen Rebuild der Terminal-Logik für das Android-Projekt im Namespace com.scto.mcs durch. Migriere die Funktionalität aus den Legacy-Dateien (MkSession, MkRootfs, TerminalBackEnd, TerminalFiles, SessionService) in die neue Clean-Architecture.
1. Projekt-Kontext & Referenzen (WICHTIG)
Nutze den beiliegenden Projekt-Tree (MCS_20260502_125638.md) als Referenz für die bestehende Dateistruktur. Die betroffenen Module sind :core:terminal, :feature:terminal, :core:files, :core:di, :core:domain und :core:editor. Achte bei allen neuen oder angepassten Dateien darauf, den Namespace com.rk.* zwingend durch com.scto.mcs.* zu ersetzen.
2. Core Terminal & Domain Logik (core/terminal & core/domain)
* TerminalSessionManager (Logik-Zentrum):
   * Erstelle/Update den Manager als Hilt @Singleton.
   * Stelle einen StateFlow<List<TerminalSession>> bereit.
   * Implementiere createNewSession(name: String): Übernimm hierbei die Kern-Logik aus der alten MkSession.kt.
   * Editor-Tab Integration: Injektiere den TabManager. Wenn eine Session erstellt wird, berechne das PWD dynamisch: Falls Settings.project_as_pwd aktiv ist, nimm den Pfad des aktuell aktiven EditorTab.
* TerminalSessionFactory & Umgebungsvariablen:
   * Integriere die gesamte Env-Map-Logik (Umgebungsvariablen) aus der alten MkSession.kt in einen neuen TerminalSessionFactory Dienst.
   * Ersetze alte SettingsViewModel.sandbox Referenzen durch Settings.sandbox (aus dem feature:settings Modul).
   * Nutze konsequent TerminalConfig für alle Pfade (proot, home, bin).
* TerminalBackEnd (Die Brücke):
   * Implementiere die Interfaces TerminalViewClient und TerminalSessionClient.
   * Übernimm die Logik für Clipboard (Copy/Paste), Skalierung (onScale) und Key-Events (Ctrl/Alt/Fn) aus der alten BackEnd-Datei.
   * WICHTIG: Die Sondertasten-Logik muss ab sofort reaktiv über das TerminalViewModel oder die VirtualKeysView abgefragt werden.
3. Core Files Repository (core/files)
* FileRepositoryImpl Erweiterung:
   * Füge saveInternalScript(name: String, content: String) hinzu: Dient zum Schreiben der stat, vmstat und setup Dateien.
   * Füge provideTerminalAsset(path: String) hinzu: Dient zum asynchronen Streamen der Shell-Skripte aus den App-Assets.
4. Feature Terminal UI & Services (feature/terminal)
* SessionService (Android Service):
   * Migriere die Logik aus dem alten SessionService (Foreground-Notification, WakeLock, Session-Liste) in eine neue, saubere @AndroidEntryPoint Service-Klasse.
   * Injektiere das FileRepository und den TerminalSessionManager via Hilt.
   * Der Service muss TerminalSessionClient implementieren, um sauber auf das Beenden einer Session reagieren zu können.
* UI (TerminalScreen & ViewModel):
   * Der TerminalScreen (Compose) muss einen ModalNavigationDrawer zur Session-Auswahl und einen HorizontalPager für die Virtual Keys (Sondertasten) enthalten.
   * Binde die native TerminalView via AndroidView (Interop) ein.
   * Verknüpfe die TerminalView reaktiv mit der activeSession aus dem TerminalViewModel.
5. Cleanup & Abschluss
* Stelle sicher, dass nirgendwo mehr com.rk.* referenziert wird.
* Lösche die alten Legacy-Dateien (MkSession.kt, MkRootfs.kt, TerminalFiles.kt und TerminalBackEnd.kt), nachdem deren Logik erfolgreich in die neuen Hilt-Services und Repositories integriert wurde.
* Bestätige am Ende die erfolgreiche Migration der Session-Logik.