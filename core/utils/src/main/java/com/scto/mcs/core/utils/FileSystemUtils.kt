package com.scto.mcs.core.utils

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileSystemUtils @Inject constructor() {
    // Funktionen für Dateizugriffe im mcs Pfad
    // FileProvider Setup Unterstützung
    
    fun getMcsDirectory(): String {
        return "/data/user/0/com.scto.mcs/files/mcs"
    }
}
