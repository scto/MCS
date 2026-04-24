package com.scto.mcs.core.editor.lsp

import com.scto.mcs.core.build_tools.lsp.models.*
import kotlinx.coroutines.flow.Flow

/**
 * Schnittstelle für die Kommunikation mit einem Language Server.
 */
interface LspClient {
    /** Sendet Dokument-Updates an den Server. */
    suspend fun didOpen(uri: String, text: String)
    suspend fun didChange(uri: String, text: String)

    /** Fordert Vervollständigungsvorschläge an. */
    suspend fun requestCompletions(uri: String, position: Position): List<CompletionItem>

    /** Stream für Fehlermeldungen (Diagnostics). */
    fun diagnosticsFlow(uri: String): Flow<List<Diagnostic>>
}