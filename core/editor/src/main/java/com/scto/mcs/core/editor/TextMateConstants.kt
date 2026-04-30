package com.scto.mcs.core.editor

import android.content.Context

import com.scto.mcs.feature.settings.SettingsViewModel
import com.scto.mcs.feature.settings.ThemeMode

// Pfade zu den Assets
const val TEXTMATE_PREFIX = "editor/languages/"
const val TEXTMATE_THEMES_PREFIX = "editor/themes/"
const val LANGUAGES_FILE = "languages.json"
const val KEYWORDS_FILE = "keywords.json"

/**
 * Generiert einen eindeutigen Cache-Key für das ColorScheme basierend auf den aktuellen Einstellungen.
 */
fun getCacheKey(context: Context, vm: SettingsViewModel, isDark: Boolean): String {
    return buildString {
        append(if (isDark) "dark" else "light")
        append("_")
        append(vm.activeThemeId ?: "default")
        append("_")
        append(vm.isAmoledEnabled)
    }
}