package com.scto.mcs.feature.terminal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scto.mcs.feature.terminal.TerminalViewModel
import com.scto.mcs.core.terminal.setup.TerminalSetupService.SetupState
import com.scto.mcs.core.ui.components.setup.TerminalSetupView

@Composable
fun TerminalScreen(viewModel: TerminalViewModel = hiltViewModel()) {
    val setupState by viewModel.setupState.collectAsState()
    val textSize by viewModel.textSize.collectAsState()
    val sessions by viewModel.sessionManager.sessions.collectAsState()
    val activeId by viewModel.sessionManager.activeSessionId.collectAsState()
    val activeSession = sessions.find { it.id == activeId }

    Box(Modifier.fillMaxSize()) {
        if (setupState !is SetupState.Completed) {
            TerminalSetupView(setupState) { viewModel.startInstallation() }
        } else {
            Column(Modifier.fillMaxSize().background(Color.Black)) {
                // Konsole
                LazyColumn(Modifier.weight(1f).padding(8.dp)) {
                    activeSession?.let { session ->
                        items(session.output) { line ->
                            Text(line, color = Color.White, fontFamily = FontFamily.Monospace, fontSize = textSize.sp)
                        }
                    }
                }
                // Eingabe
                TerminalInput { viewModel.runCommand(it) }
            }
        }
    }
}

@Composable
fun TerminalInput(onSend: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    TextField(
        value = text,
        onValueChange = { text = it },
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(focusedContainerColor = Color.DarkGray),
        placeholder = { Text("$ command...") }
    )
}
