package com.scto.mcs.core.ui.components.setup

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scto.mcs.core.terminal.setup.TerminalSetupService.SetupState

/**
 * Eine visuell ansprechende Fortschrittsanzeige für die Terminal-Installation.
 * Reagiert auf Downloads, Retries und Extraktion.
 */
@Composable
fun TerminalSetupView(
    state: SetupState,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Icon Animation basierend auf Status
            StatusIcon(state)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "System-Setup",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Status-Nachricht
            StatusMessage(state)

            Spacer(modifier = Modifier.height(32.dp))

            // Fortschrittsbalken oder Ladekreis
            ProgressIndicator(state)

            Spacer(modifier = Modifier.height(32.dp))

            // Fehler-Aktion
            if (state is SetupState.Failed) {
                Button(
                    onClick = onRetry,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Erneut versuchen")
                }
            }
        }
    }
}

@Composable
private fun StatusIcon(state: SetupState) {
    val icon = when (state) {
        is SetupState.Downloading -> Icons.Default.CloudDownload
        is SetupState.Retrying -> Icons.Default.SyncProblem
        is SetupState.Extracting -> Icons.Default.Unarchive
        is SetupState.Initializing -> Icons.Default.SettingsSuggest
        is SetupState.Completed -> Icons.Default.CheckCircle
        is SetupState.Failed -> Icons.Default.ErrorOutline
    }
    
    val color = when (state) {
        is SetupState.Failed -> MaterialTheme.colorScheme.error
        is SetupState.Retrying -> Color(0xFFFFA000) // Orange
        is SetupState.Completed -> Color(0xFF4CAF50) // Grün
        else -> MaterialTheme.colorScheme.primary
    }

    Box(
        modifier = Modifier
            .size(80.dp)
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            tint = color
        )
    }
}

@Composable
private fun StatusMessage(state: SetupState) {
    val message = when (state) {
        is SetupState.Downloading -> "Lade ${state.item} herunter..."
        is SetupState.Retrying -> "Verbindung unterbrochen. Versuch ${state.attempt} in ${state.waitTimeMs / 1000}s..."
        is SetupState.Extracting -> "Extrahiere Dateisystem..."
        is SetupState.Initializing -> "Initialisiere Shell..."
        is SetupState.Completed -> "Installation abgeschlossen!"
        is SetupState.Failed -> state.error
    }

    Text(
        text = message,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        color = if (state is SetupState.Failed) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun ProgressIndicator(state: SetupState) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        when (state) {
            is SetupState.Downloading -> {
                LinearProgressIndicator(
                    progress = state.progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                Text(
                    "${(state.progress * 100).toInt()}%",
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.labelMedium
                )
            }
            is SetupState.Retrying, is SetupState.Extracting, is SetupState.Initializing -> {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
            else -> { /* Keine Anzeige bei Erfolg oder fatalem Fehler */ }
        }
    }
}