/add core/terminal/**/*.kt feature/terminal/**/*.kt core/files/src/main/java/com/scto/mcs/core/files/repository/*.kt core/di/**/*.kt core/domain/**/*.kt core/editor/src/main/java/com/scto/mcs/core/editor/tabs/**/*.kt
Führe einen vollständigen Rebuild der Terminal-Logik durch. Migriere die Funktionalität aus den Legacy-Dateien (MkSession, MkRootfs, TerminalBackEnd, TerminalFiles, SessionService) in die neue MCS-Architektur.

STRIKTE IMPLEMENTIERUNGS-REGELN:
1. SessionService & Hilt (Kernaufgabe):
   * Nimm die vollständige Logik aus dem alten SessionService (Foreground-Notification, WakeLock, Session-Liste) und implementiere sie in der neuen @AndroidEntryPoint Klasse SessionService.
   * Injektiere das FileRepository und den TerminalSessionManager via Hilt.
   * Der Service muss TerminalSessionClient implementieren, um auf Session-Ende zu reagieren.

2. TerminalSessionManager (Logik-Zentrum):
   * Erstelle/Update den TerminalSessionManager als @Singleton.
   * Er muss einen StateFlow<List<TerminalSession>> bereitstellen.
   * Implementiere createNewSession(name: String): Nutze hierbei die Logik aus MkSession.kt.
   * Integration Editor-Tabs: Injektiere TabManager. Wenn eine Session erstellt wird, berechne das PWD: Falls Settings.project_as_pwd aktiv ist, nimm den Pfad des aktuellen EditorTab.

3. MkSession & Umgebungsvariablen:
   * Integriere die gesamte env-Map Logik aus MkSession.kt in einen TerminalSessionFactory Dienst.
   * Ersetze SettingsViewModel.sandbox Referenzen durch Settings.sandbox (aus dem feature:settings Modul).
   * Nutze TerminalConfig für alle Pfade (proot, home, bin).
4. TerminalBackEnd (Die Brücke):
   * Implementiere TerminalViewClient und TerminalSessionClient.
   * Übernimm die Logik für Clipboard (Copy/Paste), Skalierung (onScale) und Key-Events (Ctrl/Alt/Fn) aus der alten Datei.
   * WICHTIG: Die Sondertasten-Logik muss reaktiv über das TerminalViewModel oder die VirtualKeysView abgefragt werden.

5. FileRepository Erweiterung:
   * Implementiere in FileRepositoryImpl:
      * saveInternalScript(name: String, content: String): Zum Schreiben der stat/vmstat/setup Dateien.
      * provideTerminalAsset(path: String): Zum Streamen der Shell-Skripte aus den Assets.

6. UI (TerminalScreen & ViewModel):
   * Der TerminalScreen muss den ModalNavigationDrawer zur Session-Auswahl und den HorizontalPager für die Virtual Keys enthalten.
   * Binde die native TerminalView via AndroidView ein und verknüpfe sie mit der activeSession aus dem ViewModel.

7. Cleanup & Mapping:
   * Ersetze alle com.rk.* durch com.scto.mcs.*.
   * Lösche die alten Dateien MkSession.kt, MkRootfs.kt, TerminalFiles.kt und TerminalBackEnd.kt nachdem deren Logik erfolgreich in die neuen Hilt-Services/Repositories integriert wurde.
Bestätige die erfolgreiche Migration der Session-Logik und zeige die neuen Hilt-Module an.