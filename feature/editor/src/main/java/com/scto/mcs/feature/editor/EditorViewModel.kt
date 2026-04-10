package com.scto.mcs.feature.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scto.mcs.core.editor.EditorConfigManager
import com.scto.mcs.core.terminal.TerminalEnvironment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

data class EditorUiState(
    val buildOutput: String = ""
)

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val terminalEnvironment: TerminalEnvironment,
    val editorConfigManager: EditorConfigManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState

    fun buildProject(projectPath: String) {
        viewModelScope.launch {
            _uiState.value = EditorUiState(buildOutput = "Building...")
            
            val output = withContext(Dispatchers.IO) {
                try {
                    val gradlew = File(projectPath, "gradlew")
                    if (gradlew.exists()) {
                        gradlew.setExecutable(true)
                    }

                    val process = ProcessBuilder("./gradlew", "assembleDebug")
                        .directory(File(projectPath))
                        .environment().apply {
                            put("JAVA_HOME", terminalEnvironment.getEnv("JAVA_HOME") ?: "")
                            put("ANDROID_HOME", terminalEnvironment.getEnv("ANDROID_HOME") ?: "")
                        }
                        .redirectErrorStream(true)
                        .start()
                    
                    process.inputStream.bufferedReader().readText()
                } catch (e: Exception) {
                    "Build failed: ${e.message}"
                }
            }
            
            _uiState.value = EditorUiState(buildOutput = output)
        }
    }
}
