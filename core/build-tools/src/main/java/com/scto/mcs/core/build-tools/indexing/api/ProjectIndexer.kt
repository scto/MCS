package com.scto.mcs.core.build_tools.indexing.api

import com.scto.mcs.core.domain.model.Documentation
import com.scto.mcs.core.domain.model.Symbol
import kotlinx.coroutines.flow.Flow
import java.io.File

interface ProjectIndexer {
    fun indexProject(root: File): Flow<IndexingProgress>
    suspend fun searchSymbols(query: String): List<Symbol>
    suspend fun getSymbolsForFile(filePath: String): List<Symbol>
    
    /**
     * Holt die Dokumentation für ein spezifisches Symbol an einer Position.
     */
    suspend fun getDocumentation(symbol: Symbol): Documentation?
}

sealed class IndexingProgress {
    data class Scanning(val currentFile: String, val count: Int) : IndexingProgress()
    object Completed : IndexingProgress()
    data class Error(val message: String) : IndexingProgress()
}