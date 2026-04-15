package com.scto.mcs.feature.settings.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scto.mcs.core.navigation.NavigationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.scto.mcs.feature.settings.SettingsState

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val navigationManager: NavigationManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsState())
    val uiState: StateFlow<SettingsState> = _uiState.asStateFlow()

    fun onBackClicked() {
        navigationManager.popBackStack()
    }

    // General Settings
    fun setFollowSystemTheme(enabled: Boolean) {
        _uiState.update { it.copy(generalSettings = it.generalSettings.copy(followSystemTheme = enabled)) }
    }

    fun setUseDarkMode(enabled: Boolean) {
        _uiState.update { it.copy(generalSettings = it.generalSettings.copy(useDarkMode = enabled)) }
    }

    fun setUseAmoledMode(enabled: Boolean) {
        _uiState.update { it.copy(generalSettings = it.generalSettings.copy(useAmoledMode = enabled)) }
    }

    fun setDynamicColors(enabled: Boolean) {
        _uiState.update { it.copy(generalSettings = it.generalSettings.copy(dynamicColors = enabled)) }
    }

    fun setEnableGestureInDrawer(enabled: Boolean) {
        _uiState.update { it.copy(generalSettings = it.generalSettings.copy(enableGestureInDrawer = enabled)) }
    }

    // Editor Settings
    fun setShowInputMethodPickerAtStart(enabled: Boolean) {
        _uiState.update { it.copy(editorSettings = it.editorSettings.copy(showInputMethodPickerAtStart = enabled)) }
    }

    fun setFontSize(size: Float) {
        _uiState.update { it.copy(editorSettings = it.editorSettings.copy(fontSize = size)) }
    }

    fun setIndentSize(size: Int) {
        _uiState.update { it.copy(editorSettings = it.editorSettings.copy(indentSize = size)) }
    }

    fun setFontLigatures(enabled: Boolean) {
        _uiState.update { it.copy(editorSettings = it.editorSettings.copy(fontLigatures = enabled)) }
    }

    fun setStickyScroll(enabled: Boolean) {
        _uiState.update { it.copy(editorSettings = it.editorSettings.copy(stickyScroll = enabled)) }
    }

    fun setWordWrap(enabled: Boolean) {
        _uiState.update { it.copy(editorSettings = it.editorSettings.copy(wordWrap = enabled)) }
    }

    fun setShowLineNumbers(enabled: Boolean) {
        _uiState.update { it.copy(editorSettings = it.editorSettings.copy(showLineNumbers = enabled)) }
    }

    fun setUseTabs(enabled: Boolean) {
        _uiState.update { it.copy(editorSettings = it.editorSettings.copy(useTabs = enabled)) }
    }

    fun setDeleteLineOnBackspace(enabled: Boolean) {
        _uiState.update { it.copy(editorSettings = it.editorSettings.copy(deleteLineOnBackspace = enabled)) }
    }

    fun setDeleteIndentOnBackspace(enabled: Boolean) {
        _uiState.update { it.copy(editorSettings = it.editorSettings.copy(deleteIndentOnBackspace = enabled)) }
    }

    // File Explorer Settings
    fun setShowHiddenFiles(enabled: Boolean) {
        _uiState.update { it.copy(fileExplorerSettings = it.fileExplorerSettings.copy(showHiddenFiles = enabled)) }
    }

    fun loadPlugins() {
        viewModelScope.launch {
            _uiState.update { it.copy(isPluginsLoading = true) }
            // Simulate API call or local loading
            kotlinx.coroutines.delay(1000)
            _uiState.update { it.copy(isPluginsLoading = false, installedPlugins = listOf("Plugin A", "Plugin B")) }
        }
    }
}
