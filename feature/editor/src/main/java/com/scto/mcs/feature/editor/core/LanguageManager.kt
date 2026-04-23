package com.srvhive.app.editor

import android.content.Context
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Verwaltet die Initialisierung von TextMate und das Mapping von Dateiendungen zu Scopes.
 */
object LanguageManager {
    private val isInitialized = CompletableDeferred<Unit>()

    // Mapping von Dateiendung zu TextMate Scope
    private val extensionToScope = mapOf(
        "kt" to "source.kotlin",
        "java" to "source.java",
        "py" to "source.python",
        "js" to "source.js",
        "ts" to "source.ts",
        "json" to "source.json",
        "html" to "text.html.basic",
        "xml" to "text.xml",
        "md" to "text.html.markdown",
        "css" to "source.css",
        "cpp" to "source.cpp",
        "c" to "source.c",
        "sh" to "source.shell"
    )

    suspend fun init(context: Context) {
        if (isInitialized.isCompleted) return
        withContext(Dispatchers.IO) {
            try {
                FileProviderRegistry.getInstance().addFileProvider(AssetsFileResolver(context.assets))
                GrammarRegistry.getInstance().loadGrammars("editor/languages/languages.json")
                isInitialized.complete(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Ermittelt den passenden TextMate-Scope basierend auf dem Dateinamen.
     */
    fun getScopeForFileName(fileName: String): String {
        val extension = fileName.substringAfterLast('.', "").lowercase()
        return extensionToScope[extension] ?: "text.plain"
    }

    suspend fun createLanguage(scope: String): TextMateLanguage {
        isInitialized.await()
        return TextMateLanguage.create(scope, true)
    }
}