package com.scto.mcs.core.build_tools.indexing.impl

import com.scto.mcs.core.build_tools.indexing.api.IndexingProgress
import com.scto.mcs.core.build_tools.indexing.api.ProjectIndexer
import com.scto.mcs.core.domain.model.Documentation
import com.scto.mcs.core.domain.model.Symbol
import com.scto.mcs.core.domain.model.SymbolKind
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KotlinProjectIndexer @Inject constructor() : ProjectIndexer {

    private val symbolIndex = ConcurrentHashMap<String, List<Pair<Symbol, String?>>>()
    private val symbolRegex = Regex("""(class|interface|object|fun|val|var)\s+([a-zA-Z0-9_]+)""")
    private val docRegex = Regex("""/\*\*[\s\S]*?\*/""")

    override fun indexProject(root: File): Flow<IndexingProgress> = flow {
        symbolIndex.clear()
        val files = root.walkTopDown()
            .filter { it.isFile && (it.extension == "kt" || it.extension == "java") }
            .toList()

        files.forEachIndexed { index, file ->
            emit(IndexingProgress.Scanning(file.name, index + 1))
            symbolIndex[file.absolutePath] = parseFileWithDocs(file)
        }
        emit(IndexingProgress.Completed)
    }.flowOn(Dispatchers.Default)

    private fun parseFileWithDocs(file: File): List<Pair<Symbol, String?>> {
        val results = mutableListOf<Pair<Symbol, String?>>()
        try {
            val content = file.readText()
            // Suche alle Dokumentationsblöcke und Symbole
            val docBlocks = docRegex.findAll(content).toList()
            
            symbolRegex.findAll(content).forEach { match ->
                val type = match.groupValues[1]
                val name = match.groupValues[2]
                val offset = match.range.first
                
                // Suche nach dem KDoc direkt vor dem Symbol
                val doc = docBlocks.lastOrNull { it.range.last < offset && (offset - it.range.last) < 20 }?.value
                
                val kind = when (type) {
                    "class" -> SymbolKind.CLASS
                    "interface" -> SymbolKind.INTERFACE
                    "fun" -> SymbolKind.FUNCTION
                    "val", "var" -> SymbolKind.PROPERTY
                    else -> SymbolKind.FIELD
                }

                val line = content.take(offset).count { it == '\n' } + 1
                
                results.add(Symbol(name, kind, file.absolutePath, line) to doc)
            }
        } catch (e: Exception) { }
        return results
    }

    override suspend fun searchSymbols(query: String): List<Symbol> = withContext(Dispatchers.Default) {
        if (query.isBlank()) return@withContext emptyList()
        symbolIndex.values.flatten().map { it.first }.filter { 
            it.name.contains(query, ignoreCase = true) 
        }
    }

    override suspend fun getSymbolsForFile(filePath: String): List<Symbol> {
        return symbolIndex[filePath]?.map { it.first } ?: emptyList()
    }

    override suspend fun getDocumentation(symbol: Symbol): Documentation? {
        val fileSymbols = symbolIndex[symbol.filePath] ?: return null
        val match = fileSymbols.find { it.first.name == symbol.name && it.first.line == symbol.line }
        val docText = match?.second ?: return null
        
        // Bereinige KDoc Sterne und Slashes
        val cleanDoc = docText.replace(Regex("""/\*\*|\*/|^\s*\*""", RegexOption.MULTILINE), "").trim()
        
        return Documentation(symbol.name, symbol.kind, cleanDoc, symbol.filePath)
    }
}