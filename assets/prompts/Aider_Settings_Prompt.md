Bitte führe die folgenden Refactoring- und Architektur-Aufgaben in diesem Kotlin-Projekt (mcs) Schritt für Schritt aus. Das Projekt nutzt Jetpack Compose für das UI und Dagger Hilt für Dependency Injection.
1. Neues Submodul erstellen:
   * Erstelle im Ordner feature ein neues Submodul namens settings (also feature/settings).
   * Lege in diesem neuen Ordner eine build.gradle.kts an.
   * Konfiguriere die build.gradle.kts für Jetpack Compose (aktiviere buildFeatures { compose = true } und füge Standard-Compose-Abhängigkeiten wie Material3, UI und Tooling hinzu) sowie für Dagger Hilt (füge die Hilt-Plugins wie dagger.hilt.android.plugin, ksp/kapt und die entsprechenden Hilt-Bibliotheken hinzu).
   * Registriere das neue Modul, indem du include(":feature:settings") zur root settings.gradle.kts (oder settings.gradle) hinzufügst.
2. Verzeichnisstruktur anlegen:
   * Erstelle im neuen Modul die Verzeichnisstruktur: feature/settings/src/main/java/com/scto/mcs/feature/settings.
3. Dateien migrieren & anpassen:
   * Verschiebe alle Dateien aus dem bestehenden Ordner assets/src/settings in den neu erstellten Ordner feature/settings/src/main/java/com/scto/mcs/feature/settings.
   * Ändere in allen verschobenen Dateien die Package-Deklaration auf package com.scto.mcs.feature.settings.
   * Aktualisiere alle fehlerhaften Imports. Falls in den migrierten Dateien veraltetes UI-Zeug ist, weise mich darauf hin, damit wir es später in Compose umschreiben können.
4. Neue Architektur-Komponenten (Hilt & Compose) erstellen:
   * Erstelle SettingsState.kt: Eine Kotlin data class für den Compose UI-State.
   * Erstelle SettingsViewModel.kt: Ein Android ViewModel, versehen mit der Annotation @HiltViewModel und einem @Inject constructor(). Nutze Kotlin StateFlow, um den SettingsState für Compose bereitzustellen.
   * Erstelle den Unterordner di und darin die Datei SettingsModule.kt: Ein Hilt-Modul mit @Module und @InstallIn(ViewModelComponent::class) (oder SingletonComponent), um eventuelle Abhängigkeiten (z.B. DataStore oder Repositories) für das ViewModel bereitzustellen.
5. Navigation anpassen (Compose Navigation):
   * Öffne die Navigationsdateien im Modul :core:navigation.
   * Ergänze die Compose-Navigation für das neue Settings-Feature (z.B. über eine NavGraphBuilder.settingsScreen(...) Extension-Funktion).
   * Binde den neuen Screen ein und sorge dafür, dass das ViewModel innerhalb der Route-Definition per hiltViewModel() injiziert wird.
6. App-Modul überprüfen:
   * Öffne die build.gradle.kts im Modul app (oder :app).
   * Füge implementation(project(":feature:settings")) zu den Abhängigkeiten hinzu. Dies ist besonders wichtig, damit der Hilt-Compiler im App-Modul den Hilt-Graphen um das neue Settings-Modul erweitern kann.
Bitte überprüfe nach jedem Schritt, ob der reine Kotlin-Code sauber kompiliert und gib mir ein kurzes Feedback über den Fortschritt.