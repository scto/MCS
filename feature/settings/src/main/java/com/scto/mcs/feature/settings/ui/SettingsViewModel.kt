package com.scto.mcs.feature.settings.ui

import androidx.lifecycle.ViewModel
import com.scto.mcs.core.navigation.NavigationManager
import com.scto.mcs.feature.settings.R
import com.scto.mcs.feature.settings.navigation.SettingsRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val navigationManager: NavigationManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsState(
            items = listOf(
                SettingItem("app", R.string.settings_app_title, R.string.settings_app_description, SettingsRoutes.APP, SettingsSection.CONFIGURE),
                SettingItem("theme", R.string.settings_theme_title, R.string.settings_theme_description, SettingsRoutes.THEME, SettingsSection.CONFIGURE),
                SettingItem("editor", R.string.settings_editor_title, R.string.settings_editor_description, SettingsRoutes.EDITOR, SettingsSection.CONFIGURE),
                SettingItem("keybinds", R.string.settings_keybinds_title, R.string.settings_keybinds_description, SettingsRoutes.KEYBINDS, SettingsSection.CONFIGURE),
                SettingItem("git", R.string.settings_git_title, R.string.settings_git_description, SettingsRoutes.GIT, SettingsSection.CONFIGURE),
                SettingItem("terminal", R.string.settings_terminal_title, R.string.settings_terminal_description, SettingsRoutes.TERMINAL, SettingsSection.CONFIGURE),
                SettingItem("runners", R.string.settings_runners_title, R.string.settings_runners_description, SettingsRoutes.RUNNERS, SettingsSection.CONFIGURE),
                SettingItem("extension", R.string.settings_extension_title, R.string.settings_extension_description, SettingsRoutes.EXTENSION, SettingsSection.CONFIGURE),
                SettingItem("debug", R.string.settings_debug_options_title, R.string.settings_debug_options_description, SettingsRoutes.DEBUG, SettingsSection.CONFIGURE),
                SettingItem("lsp", R.string.settings_lsp_title, R.string.settings_lsp_description, SettingsRoutes.LSP, SettingsSection.CONFIGURE),
                SettingItem("language", R.string.settings_language_title, R.string.settings_language_description, SettingsRoutes.LANGUAGE, SettingsSection.CONFIGURE),
                SettingItem("about", R.string.settings_about_title, R.string.settings_about_description, SettingsRoutes.ABOUT, SettingsSection.INFORMATION),
                SettingItem("support", R.string.settings_support_title, R.string.settings_support_description, SettingsRoutes.SUPPORT, SettingsSection.INFORMATION)
            )
        )
    )
    val uiState: StateFlow<SettingsState> = _uiState.asStateFlow()

    fun navigateTo(route: String) {
        navigationManager.navigateTo(route)
    }

    fun onBackClicked() {
        navigationManager.popBackStack()
    }
}
