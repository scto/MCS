package com.scto.mcs.core.domain.model

/**
 * Definiert die verschiedenen Arten, wie der Dateibaum in der IDE dargestellt werden kann.
 */
enum class FileTreeMode {
    /** Die klassische 1:1 Darstellung des Dateisystems. */
    EXPLORER,
    
    /** * Filtert die Ansicht auf logische Module. 
     * Zeigt vorrangig Verzeichnisse an, die Build-Konfigurationen (z.B. build.gradle.kts) enthalten. 
     */
    MODUL,
    
    /** * Optimiert für Quellcode-Strukturen. 
     * Verknüpft leere Zwischenverzeichnisse zu einer Package-Notation (z.B. 'com.example.app'). 
     */
    PACKAGE
}