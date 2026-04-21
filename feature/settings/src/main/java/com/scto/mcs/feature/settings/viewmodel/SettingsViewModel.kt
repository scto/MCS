package com.scto.mcs.feature.settings.viewmodel

import androidx.lifecycle.ViewModel
import com.scto.mcs.core.navigation.NavigationManager
import com.scto.mcs.feature.settings.R
import com.scto.mcs.feature.settings.navigation.SettingsRoutes
import com.scto.mcs.feature.settings.ui.SettingItem
import com.scto.mcs.feature.settings.ui.SettingsState
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
                SettingItem("app", R.string.settings_app_title, R.string.settings_app_description, SettingsRoutes.APP),
                SettingItem("theme", R.string.settings_theme_title, R.string.settings_theme_description, SettingsRoutes.THEME),
                SettingItem("editor", R.string.settings_editor_title, R.string.settings_editor_description, SettingsRoutes.EDITOR),
                SettingItem("keybinds", R.string.settings_keybinds_title, R.string.settings_keybinds_description, SettingsRoutes.KEYBINDS),
                SettingItem("git", R.string.settings_git_title, R.string.settings_git_description, SettingsRoutes.GIT),
                SettingItem("terminal", R.string.settings_terminal_title, R.string.settings_terminal_description, SettingsRoutes.TERMINAL),
                SettingItem("runners", R.string.settings_runners_title, R.string.settings_runners_description, SettingsRoutes.RUNNERS),
                SettingItem("extension", R.string.settings_extension_title, R.string.settings_extension_description, SettingsRoutes.EXTENSION),
                SettingItem("debug", R.string.settings_debug_options_title, R.string.settings_debug_options_description, SettingsRoutes.DEBUG),
                SettingItem("lsp", R.string.settings_lsp_title, R.string.settings_lsp_description, SettingsRoutes.LSP),
                SettingItem("language", R.string.settings_language_title, R.string.settings_language_description, SettingsRoutes.LANGUAGE),
                SettingItem("about", R.string.settings_about_title, R.string.settings_about_description, SettingsRoutes.ABOUT),
                SettingItem("support", R.string.settings_support_title, R.string.settings_support_description, SettingsRoutes.SUPPORT)
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
