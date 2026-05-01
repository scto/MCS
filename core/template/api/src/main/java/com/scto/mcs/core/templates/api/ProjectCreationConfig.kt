package com.scto.mcs.core.templates.api

data class ProjectCreationConfig(
    val appName: String,
    val packageName: String,
    val minSdk: Int,
    val targetSdk: Int,
    val language: Language,
    val useKotlinDsl: Boolean
) {
    enum class Language {
        KOTLIN, JAVA
    }
}
