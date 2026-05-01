package com.scto.mcs.core.templates.api

data class TemplateMetadata(
    val id: String,
    val name: String,
    val type: TemplateType,
    val thumbnailUri: String,
    val description: String,
    val version: String,
    val sourceUrl: String
)
