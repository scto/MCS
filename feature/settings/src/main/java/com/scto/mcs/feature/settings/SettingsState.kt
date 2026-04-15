package com.scto.mcs.feature.settings

import androidx.compose.runtime.Immutable

@Immutable
data class SettingsState(
    val generalSettings: GeneralSettings = GeneralSettings(),
    val editorSettings: EditorSettings = EditorSettings(),
    val fileExplorerSettings: FileExplorerSettings = FileExplorerSettings(),
    val isPluginsLoading: Boolean = false,
    val installedPlugins: List<String> = emptyList() // Placeholder for plugins
)

@Immutable
data class GeneralSettings(
    val followSystemTheme: Boolean = false,
    val useDarkMode: Boolean = true, // Default to dark mode as per design
    val useAmoledMode: Boolean = false,
    val dynamicColors: Boolean = false,
    val enableGestureInDrawer: Boolean = true
)

@Immutable
data class EditorSettings(
    val showInputMethodPickerAtStart: Boolean = false,
    val fontSize: Float = 14f, // Default 14 sp
    val indentSize: Int = 4, // Default 4 spaces
    val fontLigatures: Boolean = false,
    val stickyScroll: Boolean = false,
    val wordWrap: Boolean = true,
    val showLineNumbers: Boolean = true,
    val useTabs: Boolean = false, // Default to spaces
    val deleteLineOnBackspace: Boolean = false,
    val deleteIndentOnBackspace: Boolean = false
)

@Immutable
data class FileExplorerSettings(
    val showHiddenFiles: Boolean = false
)
