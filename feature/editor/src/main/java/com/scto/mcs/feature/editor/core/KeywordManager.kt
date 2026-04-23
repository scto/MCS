package com.srvhive.app.editor

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

/**
 * Lädt Sprachspezifische Keywords für die Autovervollständigung aus einer JSON-Datei.
 */
object KeywordManager {
    private val isInitialized = CompletableDeferred<Unit>()
    private lateinit var keywords: Map<String, List<String>>

    suspend fun init(context: Context) {
        if (isInitialized.isCompleted) return

        withContext(Dispatchers.IO) {
            try {
                context.assets.open("editor/keywords.json").use { input ->
                    val gson = Gson()
                    val type = object : TypeToken<Map<String, List<String>>>() {}
                    keywords = gson.fromJson(InputStreamReader(input), type.type)
                }
                isInitialized.complete(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getKeywords(scope: String): List<String>? {
        isInitialized.await()
        return keywords[scope]
    }
}