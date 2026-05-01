package com.scto.mcs.core.templates.api

interface TemplateQueryService {
    fun getTotalTemplateCount(): Int
    fun getTemplateCountByType(type: TemplateType): Int
    fun getAllTemplates(): List<TemplateMetadata>
    fun getTemplatesByType(type: TemplateType): List<TemplateMetadata>
}
