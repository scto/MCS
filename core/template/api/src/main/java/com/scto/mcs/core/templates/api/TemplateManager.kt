package com.scto.mcs.core.templates.api

import kotlinx.coroutines.flow.Flow

interface TemplateManager {
    suspend fun downloadTemplates()
    suspend fun installTemplates()
    suspend fun updateTemplates()
    suspend fun upgradeTemplates()
    suspend fun clearTemplates()
}
