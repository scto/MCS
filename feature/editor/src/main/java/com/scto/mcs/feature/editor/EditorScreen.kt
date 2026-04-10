package com.scto.mcs.feature.editor

import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.scto.mcs.core.editor.EditorConfigManager
import com.scto.mcs.core.ui.components.MCSToolbar
import com.scto.mcs.core.ui.theme.MCSTheme
import io.github.rosemoe.sora.widget.CodeEditor

@Composable
fun EditorScreen(
    projectPath: String,
    viewModel: EditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val editorConfigManager = viewModel.editorConfigManager

    MCSTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            MCSToolbar(title = "Editor") {
                Button(onClick = { viewModel.buildProject(projectPath) }) {
                    Text("Build")
                }
            }
            
            AndroidView(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                factory = { context ->
                    CodeEditor(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        isLineNumberEnabled = true
                        isAutoCompletionEnabled = true
                    }
                },
                update = { editor ->
                    // Syntax Highlighting Konfiguration
                    val colors = editorConfigManager.getThemeColors()
                    // Hier würde die Integration der TextMate-Grammatiken erfolgen
                }
            )
            
            Surface(
                modifier = Modifier.height(200.dp).fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface
            ) {
                Text(
                    text = uiState.buildOutput,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}
