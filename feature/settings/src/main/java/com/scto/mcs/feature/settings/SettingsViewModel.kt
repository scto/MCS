package com.scto.mcs.feature.settings

import android.content.Context
import android.net.Uri

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.ViewModel

import org.json.JSONObject
import java.io.File

/**
 * Datenmodell für ein benutzerdefiniertes Theme.
 */
data class CustomTheme(
    val id: String,
    val name: String,
    val lightScheme: ColorScheme,
    val darkScheme: ColorScheme,
    val isRemovable: Boolean = true
)

/**
 * ViewModel zur Verwaltung aller App-Einstellungen und Themes.
 */
class SettingsViewModel : ViewModel() {
    // UI-Zustände für das Design
    var themeMode by mutableStateOf(ThemeMode.SYSTEM)
    var isDynamicColorEnabled by mutableStateOf(true)
    var isAmoledEnabled by mutableStateOf(false)
    val installedThemes = mutableStateListOf<CustomTheme>()
    var activeThemeId by mutableStateOf<String?>(null)

    // Zustände für die Dateibaum-Konfiguration
    var fileSortBy by mutableStateOf(FileSortBy.NAME)
    var fileSortOrder by mutableStateOf(FileSortOrder.ASCENDING)
    var showHiddenFiles by mutableStateOf(false)

    /**
     * Lädt alle verfügbaren Themes aus den Assets und dem internen Speicher.
     */
    fun loadAllThemes(context: Context) {
        if (installedThemes.isNotEmpty()) return
        
        // 1. System-Themes laden
        try {
            context.assets.list("themes")?.filter { it.endsWith(".json") }?.forEach { fileName ->
                val json = context.assets.open("themes/$fileName").bufferedReader().use { it.readText() }
                installThemeFromJson(json, isRemovable = false)
            }
        } catch (e: Exception) { e.printStackTrace() }

        // 2. Benutzer-Themes laden
        val themeDir = File(context.filesDir, "themes")
        if (themeDir.exists()) {
            themeDir.listFiles { f -> f.extension == "json" }?.forEach { file ->
                installThemeFromJson(file.readText(), isRemovable = true)
            }
        }
    }

    fun installThemeFromUri(context: Context, uri: Uri): Boolean {
        return try {
            val jsonString = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
            if (jsonString != null && installThemeFromJson(jsonString, isRemovable = true)) {
                saveThemeToInternalStorage(context, jsonString)
                true
            } else false
        } catch (e: Exception) { e.printStackTrace(); false }
    }

    private fun saveThemeToInternalStorage(context: Context, jsonString: String) {
        try {
            val json = JSONObject(jsonString)
            val id = json.optString("id", "theme_${System.currentTimeMillis()}")
            val themeDir = File(context.filesDir, "themes")
            if (!themeDir.exists()) themeDir.mkdirs()
            File(themeDir, "$id.json").writeText(jsonString)
        } catch (e: Exception) { e.printStackTrace() }
    }

    fun installThemeFromJson(jsonString: String, isRemovable: Boolean): Boolean {
        return try {
            val json = JSONObject(jsonString)
            val id = json.getString("id")
            val name = json.getString("name")
            
            val lightJson = json.getJSONObject("light")
            val darkJson = json.getJSONObject("dark")

            val lightBase = if (lightJson.has("baseColors")) lightJson.getJSONObject("baseColors") else lightJson
            val darkBase = if (darkJson.has("baseColors")) darkJson.getJSONObject("baseColors") else darkJson

            val newTheme = CustomTheme(
                id = id,
                name = name,
                lightScheme = parseColors(lightBase, isDark = false),
                darkScheme = parseColors(darkBase, isDark = true),
                isRemovable = isRemovable
            )
            installedThemes.removeAll { it.id == id }
            installedThemes.add(newTheme)
            true
        } catch (e: Exception) { false }
    }

    private fun parseColors(obj: JSONObject, isDark: Boolean): ColorScheme {
        val getColor = { key: String, fallback: Color -> 
            try { Color(android.graphics.Color.parseColor(obj.getString(key))) } catch(e: Exception) { fallback }
        }
        return if (isDark) {
            darkColorScheme(
                primary = getColor("primary", Color(0xFFAAC7FF)),
                onPrimary = getColor("onPrimary", Color(0xFF0A305F)),
                background = getColor("background", Color(0xFF111318)),
                onBackground = getColor("onBackground", Color(0xFFE2E2E9)),
                surface = getColor("surface", Color(0xFF111318)),
                onSurface = getColor("onSurface", Color(0xFFE2E2E9))
            )
        } else {
            lightColorScheme(
                primary = getColor("primary", Color(0xFF415F91)),
                onPrimary = getColor("onPrimary", Color(0xFFFFFFFF)),
                background = getColor("background", Color(0xFFF9F9FF)),
                onBackground = getColor("onBackground", Color(0xFF1A1B21)),
                surface = getColor("surface", Color(0xFFF9F9FF)),
                onSurface = getColor("onSurface", Color(0xFF1A1B21))
            )
        }
    }

    @Composable
    fun getActiveCustomColorScheme(): ColorScheme? {
        val theme = installedThemes.find { it.id == activeThemeId } ?: return null
        val isDark = when(themeMode) {
            ThemeMode.DARK -> true
            ThemeMode.LIGHT -> false
            ThemeMode.SYSTEM -> isSystemInDarkTheme()
        }
        return if (isDark) theme.darkScheme else theme.lightScheme
    }

    fun deleteTheme(context: Context, theme: CustomTheme) {
        if (!theme.isRemovable) return
        val themeDir = File(context.filesDir, "themes")
        File(themeDir, "${theme.id}.json").apply { if (exists()) delete() }
        installedThemes.remove(theme)
        if (activeThemeId == theme.id) activeThemeId = null
    }

    fun setTheme(mode: ThemeMode) { themeMode = mode }
    fun toggleDynamicColor(enabled: Boolean) { isDynamicColorEnabled = enabled }
    fun toggleAmoled(enabled: Boolean) { isAmoledEnabled = enabled }
    fun updateFileSort(sortBy: FileSortBy) { fileSortBy = sortBy }
    fun updateFileOrder(order: FileSortOrder) { fileSortOrder = order }
    fun toggleHiddenFiles(show: Boolean) { showHiddenFiles = show }
}