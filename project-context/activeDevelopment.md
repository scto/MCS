## Aktueller Fokus
 * **Massives Architektur-Refactoring & Konsolidierung:** Das gesamte Projekt wird aktuell in den Namespace com.scto.mcs migriert. Alte Referenzen (com.rk, com.srvhive) werden eliminiert.
 * **Feature-Integration:** Die Funktionalität aus dem veralteten xed-Verzeichnis wird in die regulären :feature Module (insbesondere :feature:settings) integriert und konsolidiert.
## Nächste Schritte
 1. Abschluss der feature:settings XED-Migration (Ordner für Ordner).
 2. Zentralisierung aller String-Ressourcen aus allen Modulen in :core:resources.
 3. Ersetzen aller hartkodierten UI-Strings in Jetpack Compose durch stringResource(R.string...).
 4. Abschließender Build-Test des :app Moduls.
## Getroffene Entscheidungen
 * **Kein XML mehr:** Das UI wird ausschließlich in Compose geschrieben. Alt-Code wird umgeschrieben.
 * **Strikte Modulgrenzen:** Feature-Module dürfen nicht direkt miteinander kommunizieren, sondern nur über das Core-Domain-Modul oder Navigationsevents.
 * **Dynamisches Aider-Prompting:** Aufgrund von Context-Window-Limits (Token-Limits) werden Refactorings streng nach Modulen unterteilt und die Datei MCS_20260506_143519.md (Project Tree) dient als dynamische Map für Imports, anstatt Aider alles lesen zu lassen.
## Bekannte Probleme / Risiken
 * "Token Limit Exceeded": Zu viele Dateien gleichzeitig in den KI-Kontext zu laden, führt zu Abbrüchen. Workaround: Strikte /clear und /drop Routinen zwischen den Refactoring-Schritten anwenden.
 * "R is not resolved": Beim Verschieben von Dateien können die generierten Android-Ressourcen-Klassen den Bezug verlieren. Muss durch Aktualisierung der Imports auf com.scto.mcs.core.resources.R gelöst werden.
