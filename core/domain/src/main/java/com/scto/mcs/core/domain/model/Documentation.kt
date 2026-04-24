package com.scto.mcs.core.domain.model

/**
 * Repräsentiert die extrahierte Dokumentation eines Symbols.
 */
data class Documentation(
    val symbolName: String,
    val symbolKind: SymbolKind,
    val content: String, // Der rohe KDoc/Javadoc Text
    val location: String // Dateipfad oder Package
)