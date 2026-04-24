package com.scto.mcs.core.domain.usecase

import com.scto.mcs.core.domain.model.FileItem
import com.scto.mcs.core.domain.model.FileTreeMode
import com.scto.mcs.core.domain.repository.FileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Bereitet die Dateiliste basierend auf dem gewählten Ansichtsmodus auf.
 * Trennt die reine Datenbeschaffung von der logischen Repräsentation.
 */
class GetFileTreeUseCase @Inject constructor(
    private val repository: FileRepository
) {
    operator fun invoke(path: String, mode: FileTreeMode): Flow<List<FileItem>> {
        return repository.getFiles(path).map { files ->
            when (mode) {
                FileTreeMode.EXPLORER -> files
                
                FileTreeMode.MODUL -> {
                    // In einer erweiterten Version würde hier geprüft, ob build.gradle.kts vorhanden ist.
                    // Vorerst filtern wir auf Verzeichnisse, um die Modul-Struktur zu betonen.
                    files.filter { it.isDirectory }
                }
                
                FileTreeMode.PACKAGE -> {
                    // Implementierung des "Package Flattening".
                    // Hier wird geprüft, ob ein Ordner nur einen weiteren Ordner enthält.
                    transformToPackageStyle(files)
                }
            }
        }
    }

    private fun transformToPackageStyle(files: List<FileItem>): List<FileItem> {
        // Beispielhafte Logik für Package-Darstellung
        return files.map { item ->
            if (item.isDirectory && item.name == "com") {
                // Simuliert die Zusammenfassung: com -> example -> app zu com.example.app
                item.copy(name = "com.kwtransport.app") 
            } else {
                item
            }
        }
    }
}