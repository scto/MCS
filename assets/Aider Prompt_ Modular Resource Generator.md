Aider Prompt: Deep Scan & Resource Generation for Multi-Module
Handle als Senior Android Architect. Deine Aufgabe ist die systematische Erstellung und Befüllung von Ressourcen-Ordnern in einem Kotlin-basierten Multi-Module-Projekt.
1. Exklusiver Scope (Modul-Fokus)
Bearbeite ausschließlich Code und Ressourcen innerhalb der folgenden Pfade:
* :app (Verzeichnis /app)
* :core:${submodule} (Alle Verzeichnisse direkt unter /core/, z. B. /core/database, /core/ui)
* :feature:${submodule} (Alle Verzeichnisse direkt unter /feature/, z. B. /feature/auth, /feature/home)
Ignoriere alle anderen Verzeichnisse wie buildSrc, gradle, oder Root-Konfigurationsdateien, sofern sie nicht zwingend für den res-Check benötigt werden.
2. Infrastruktur-Mandat
Prüfe für jedes identifizierte Submodul (z. B. feature/login/src/main/):
* Existiert der Ordner res? Wenn nein, erstelle src/main/res.
* Erstelle darin die Basis-Struktur: values/, drawable/.
3. Ressourcen-Erkennung (Kotlin & Compose)
Analysiere alle .kt Dateien in den oben genannten Pfaden auf fehlende Ressourcen-Referenzen:
* XML/Standard: R.string.xyz, R.drawable.xyz, R.color.xyz, R.dimen.xyz.
* Jetpack Compose: stringResource(R.string.xyz), painterResource(R.drawable.xyz), colorResource(R.color.xyz).
4. Regeln für die Generierung
Falls eine Ressource im Code referenziert wird, aber in den res-Dateien des jeweiligen Moduls fehlt:
1. Dateiname: Nutze strings.xml, colors.xml oder dimens.xml.
2. Naming Convention: - Nutze snake_case.
   * WICHTIG: Nutze den Namen des Submoduls als Prefix, um Ressourcen-Clashes zu vermeiden.
   * Beispiel: Modul :feature:login referenziert R.string.welcome_text -> Erzeuge <string name="login_welcome_text">Welcome Text</string>.
3. Inhalt: - Strings erhalten den Variablennamen als Wert (mit Leerzeichen statt Unterstrichen).
   * Farben erhalten #FFFFFF (oder eine sinnvolle Standardfarbe basierend auf dem Namen).
   * Drawables erhalten eine minimale <vector> XML-Datei als Platzhalter.
5. Iterativer Workflow
1. Scanne zuerst :core:* und melde gefundene Lücken.
2. Scanne dann :feature:* und melde Lücken.
3. Schließe mit :app ab.
4. Führe die Änderungen erst nach der Analyse gesammelt pro Modul durch.
Beginne jetzt mit dem Scan der Submodule in :core, :feature und dem :app Modul.