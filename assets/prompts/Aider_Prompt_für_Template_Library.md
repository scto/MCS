Erstelle eine vollständige Kotlin-basierte Android-Bibliothek für das Projekt-Template-Management. Arbeite nach den Prinzipien der Clean Architecture und SOLID. Verwende ausschließlich Kotlin, Coroutines, Flows (StateFlow/SharedFlow) und injiziere Coroutine Dispatchers über den Konstruktor für Testbarkeit.
Bitte lege die benötigten Verzeichnisse und Dateien für die folgenden drei Module an und implementiere die geforderte Logik:

1. Modulstruktur & Build-Dateien
Lege die grundlegende Verzeichnisstruktur für drei Module an:
* :core:template:api (Paket: com.scto.mcs.core.templates.api)
* :core:template:data (Paket: com.scto.mcs.core.templates.data)
* :core:template:impl (Paket: com.scto.mcs.core.templates.impl)
Erstelle für jedes Modul eine grundlegende build.gradle.kts Datei. Das impl Modul benötigt Abhängigkeiten zum api und data Modul.

2. API Modul (:core:template:api)
Erstelle hier nur Interfaces, Domain-Models und Exceptions:
* ProjectCreationConfig (Data Class): Enthält appName (String), packageName (String), minSdk (Int), targetSdk (Int), language (Enum: Kotlin/Java), useKotlinDsl (Boolean).
* ProjectLocationProvider (Interface): Liefert den Root-Zielpfad für die Projekterstellung (wird später von außen injiziert).
* TemplateManager (Interface): Definiert suspend funs/Flows für downloadTemplates(), installTemplates(), updateTemplates(), upgradeTemplates() und clearTemplates().
* Metadaten & Abfrage-API für die UI:
   * TemplateType (Enum): ANDROID_NATIVE, CMAKE, FLUTTER, LIBGDX.
   * TemplateMetadata (Data Class): id/name, type (TemplateType), thumbnailUri (String: lokaler Pfad/URI für UI-Bilder), description (String), version (String), sourceUrl (String).
   * TemplateQueryService (Interface): Funktionen zum Beziehen der Gesamtanzahl an Templates, Anzahl gefiltert nach Typ, Liste aller TemplateMetadata und Liste von TemplateMetadata gefiltert nach Typ.

3. Data Modul (:core:template:data)
* TemplateVersionRepository: Implementiere ein Repository, das die zentrale Versionsverwaltung übernimmt. Es hält die Gradle-Version und Versionen aller verwendeten Bibliotheken (für die Templates). Es muss Funktionen bieten, um Versionen als Flow/Suspend abzufragen, bestimmte Versionen zu überschreiben und zu aktualisieren.

4. Impl Modul (:core:template:impl)
Implementiere hier die Geschäftslogik, Dateiverwaltung und das Netzwerk-Handling:

* TemplateDataSource: Lädt Template-ZIP-Archive von einer remote URL (z.B. GitHub/CDN) herunter und entpackt sie on-the-fly mittels ZipInputStream in ein lokales templateStorageDir. WICHTIG: Implementiere hier zwingend einen Zip-Slip-Vulnerability-Schutz (Prüfung via canonicalPath)!

* TemplateManagerImpl: Implementiert das TemplateManager Interface. Orchestriert Downloads, Entpacken und die lokale Verzeichnisverwaltung (templateStorageDir).

* GradleWrapperManager: Lädt die gradle-wrapper.jar und .properties herunter (die Version kommt aus dem TemplateVersionRepository) und platziert sie im Zielprojekt unter gradle/wrapper/.

* ProjectGenerator: Nimmt eine ProjectCreationConfig entgegen und generiert das Projekt am Pfad des ProjectLocationProvider.

   * Erstelle hier die Logik, um dynamisch eine gradle/libs.versions.toml aus den Daten des TemplateVersionRepository zu generieren.

   * Die zu erstellenden build.gradle.kts Dateien des Templates müssen auf diese TOML-Datei verweisen.

* TemplateQueryServiceImpl: Implementiert das TemplateQueryService Interface. Parst die lokalen entpackten Templates, extrahiert Metadaten und löst die lokalen Dateipfade für die thumbnailUri auf, damit ein UI-Modul diese Bilder z.B. via Coil laden kann, ohne sie selbst als Assets halten zu müssen.

Bitte setze diese Anforderungen schrittweise in Code um und achte auf sauberes Error-Handling.