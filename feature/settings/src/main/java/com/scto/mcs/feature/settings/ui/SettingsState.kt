package com.scto.mcs.feature.settings.ui

import androidx.compose.runtime.Immutable
import com.scto.mcs.core.utils.LogConfigState
import com.scto.mcs.core.utils.ThemeState

@Immutable
data class SettingsState(
    val generalSettings: GeneralSettings = GeneralSettings(),
    val editorSettings: EditorSettings = EditorSettings(),
    val fileExplorerSettings: FileExplorerSettings = FileExplorerSettings(),
    val themeState: ThemeState? = null,
    val logConfigState: LogConfigState? = null,
    val isPluginsLoading: Boolean = false,
    val installedPlugins: List<String> = emptyList()
)

@Immutable
data class GeneralSettings(
    val enableGestureInDrawer: Boolean = true
)

@Immutable
data class EditorSettings(
    val showInputMethodPickerAtStart: Boolean = false,
    val fontSize: Float = 14f,
    val indentSize: Int = 4,
    val fontLigatures: Boolean = false,
    val stickyScroll: Boolean = false,
    val wordWrap: Boolean = true,
    val showLineNumbers: Boolean = true,
    val useTabs: Boolean = false,
    val deleteLineOnBackspace: Boolean = false,
    val deleteIndentOnBackspace: Boolean = false
)

@Immutable
data class FileExplorerSettings(
    val showHiddenFiles: Boolean = false
)
