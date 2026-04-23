package com.srvhive.app.editor

import android.content.Context
import android.graphics.Typeface
import androidx.compose.ui.text.font.Font
import java.io.File

/**
 * Verwaltet das Laden und Cachen von Schriftarten für den Editor.
 */
object FontCache {
    private val cachedFonts = mutableMapOf<String, CachedFont>()

    data class CachedFont(val typeface: Typeface)

    fun getTypeface(context: Context, path: String, isAsset: Boolean): Typeface? {
        val cacheKey = if (isAsset) "asset_$path" else "file_$path"
        
        cachedFonts[cacheKey]?.let { return it.typeface }

        return try {
            val tf = if (isAsset) {
                Typeface.createFromAsset(context.assets, path)
            } else {
                val file = File(path)
                if (file.exists()) Typeface.createFromFile(file) else Typeface.MONOSPACE
            }
            cachedFonts[cacheKey] = CachedFont(tf)
            tf
        } catch (e: Exception) {
            e.printStackTrace()
            Typeface.MONOSPACE
        }
    }
}