package com.scto.mcs.feature.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scto.mcs.core.terminal.TerminalEnvironment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SetupUiState(
    val jdkVersion: String = "17",
    val sdkVersion: String = "35",
    val isInstalling: Boolean = false,
    val statusMessage: String = ""
)

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val terminalEnvironment: TerminalEnvironment
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState

    fun setJdk(version: String) {
        _uiState.value = _uiState.value.copy(jdkVersion = version)
    }

    fun setSdk(version: String) {
        _uiState.value = _uiState.value.copy(sdkVersion = version)
    }

    fun startSetup() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isInstalling = true, statusMessage = "Initialisiere...")
            terminalEnvironment.initializeEnvironment()
            
            _uiState.value = _uiState.value.copy(statusMessage = "Installiere JDK ${_uiState.value.jdkVersion}...")
            delay(2000) // Simulate work
            terminalEnvironment.setEnv("JAVA_HOME", "/path/to/jdk${_uiState.value.jdkVersion}")
            
            _uiState.value = _uiState.value.copy(statusMessage = "Installiere Android SDK ${_uiState.value.sdkVersion}...")
            delay(2000) // Simulate work
            terminalEnvironment.setEnv("ANDROID_HOME", "/path/to/sdk${_uiState.value.sdkVersion}")
            
            _uiState.value = _uiState.value.copy(isInstalling = false, statusMessage = "Setup abgeschlossen!")
        }
    }
}
