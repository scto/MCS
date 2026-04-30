package com.scto.mcs.core.editor

import android.content.Context

import com.scto.mcs.feature.settings.SettingsViewModel
import com.scto.mcs.feature.settings.ThemeMode

import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import org.eclipse.tm4e.core.registry.IThemeSource

import java.io.ByteArrayInputStream

/**
 * Verwaltet das Laden und Patchen von TextMate Themes.
 */
object ThemeManager {
    private const val DARCULA = "editor/themes/darcula.json"
    private const val QUIETLIGHT = "editor/themes/quietlight.json"

    suspend fun createColorScheme(
        context: Context, 
        vm: SettingsViewModel, 
        patchArgs: Editor.PatchArgs
    ): TextMateColorScheme = withContext(Dispatchers.IO) {
        
        val themePath = if (patchArgs.isDarkMode) DARCULA else QUIETLIGHT
        val themeName = if (patchArgs.isDarkMode) "darcula" else "quietlight"

        // Basis-Theme aus Assets laden
        val inputStream = context.assets.open(themePath)
        val themeSource = IThemeSource.fromInputStream(inputStream, themeName, null)
        val themeModel = ThemeModel(themeSource)

        // XedColorScheme sorgt für die M3-Integration
        MCSColorScheme(patchArgs, themeModel)
    }
}