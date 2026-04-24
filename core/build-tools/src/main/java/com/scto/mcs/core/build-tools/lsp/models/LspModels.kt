package com.scto.mcs.core.build_tools.lsp.models

import android.net.Uri

/**
 * Repräsentiert eine Position in einer Textdatei.
 */
data class Position(val line: Int, val character: Int)

/**
 * Ein Bereich innerhalb einer Datei.
 */
data class Range(val start: Position, val end: Position)

/**
 * Repräsentiert einen Ort in einer spezifischen Datei (für Definitionen).
 */
data class Location(
    val uri: String,
    val range: Range
)

/**
 * Diagnose-Informationen (Fehler/Warnungen).
 */
data class Diagnostic(
    val range: Range,
    val message: String,
    val severity: DiagnosticSeverity
)

enum class DiagnosticSeverity {
    ERROR, WARNING, INFORMATION, HINT
}

/**
 * Vervollständigungsvorschlag.
 */
data class CompletionItem(
    val label: String,
    val detail: String? = null,
    val insertText: String? = null,
    val kind: CompletionItemKind = CompletionItemKind.TEXT
)

enum class CompletionItemKind {
    TEXT, METHOD, FUNCTION, CONSTRUCTOR, FIELD, VARIABLE, CLASS, INTERFACE
}