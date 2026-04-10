package com.scto.mcs.feature.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scto.mcs.core.ui.components.MCSButton
import com.scto.mcs.core.ui.theme.MCSTheme

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToEditor: (String) -> Unit
) {
    var showCloneDialog by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isCloned) {
        if (uiState.isCloned) {
            onNavigateToEditor(uiState.clonedProjectPath ?: "")
            viewModel.resetCloneState()
        }
    }

    MCSTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("MCS Dashboard", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(32.dp))
                
                MCSButton(text = "Projekt öffnen", onClick = { /* TODO */ })
                Spacer(modifier = Modifier.height(16.dp))
                MCSButton(text = "Projekt erstellen", onClick = { /* TODO */ })
                Spacer(modifier = Modifier.height(16.dp))
                MCSButton(text = "Repository klonen", onClick = { showCloneDialog = true })
                Spacer(modifier = Modifier.height(16.dp))
                MCSButton(text = "Einstellungen", onClick = { /* TODO */ })
            }

            if (uiState.isCloning) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        if (showCloneDialog) {
            CloneProjectDialog(
                onDismiss = { showCloneDialog = false },
                onClone = { url ->
                    viewModel.cloneRepository(url)
                    showCloneDialog = false
                }
            )
        }
    }
}
