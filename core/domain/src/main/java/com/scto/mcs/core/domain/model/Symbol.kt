package com.scto.mcs.core.domain.model

/**
 * Repräsentiert ein gefundenes Symbol (Klasse, Methode, Variable) im Projekt.
 */
data class Symbol(
    val name: String,
    val kind: SymbolKind,
    val filePath: String,
    val line: Int,
    val column: Int = 0,
    val containerName: String? = null // z.B. Klassenname für eine Methode
)

enum class SymbolKind {
    CLASS, INTERFACE, FUNCTION, PROPERTY, FIELD, ENUM, OBJECT
}