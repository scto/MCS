package com.scto.mcs.core.ui.components.sidepanel.git

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Git Management Panel mit der im Screenshot angedeuteten Struktur.
 */
@Composable
fun GitManagerContent() {
    Row(modifier = Modifier.fillMaxSize()) {
        // Linke Button-Liste für Git-Aktionen
        Column(
            modifier = Modifier
                .width(40.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GitActionButton(Icons.Default.History, "History")
            GitActionButton(Icons.Default.AltRoute, "Branches")
            GitActionButton(Icons.Default.CloudDownload, "Remotes")
            Divider(modifier = Modifier.padding(vertical = 4.dp))
            GitActionButton(Icons.Default.PlaylistAddCheck, "Stage All")
            GitActionButton(Icons.Default.Refresh, "Refresh")
            GitActionButton(Icons.Default.Settings, "Git Settings")
        }

        // Haupt-Git-Inhalt (Changes/Commits)
        Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
            Text("Änderungen", style = MaterialTheme.typography.titleSmall)
            
            LazyColumn(modifier = Modifier.weight(1f)) {
                // Beispiel-Einträge basierend auf dem Screenshot
                items(5) { index ->
                    GitFileEntry(fileName = "Datei_$index.kt")
                }
            }
        }
    }
}

@Composable
fun GitActionButton(icon: androidx.compose.ui.graphics.vector.ImageVector, description: String) {
    IconButton(onClick = { /* Git Action */ }, modifier = Modifier.size(32.dp)) {
        Icon(icon, contentDescription = description, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun GitFileEntry(fileName: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(fileName, style = MaterialTheme.typography.labelMedium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { /* Stage */ }) { Text("Stage", style = MaterialTheme.typography.labelSmall) }
                TextButton(onClick = { /* Discard */ }) { Text("Discard", style = MaterialTheme.typography.labelSmall) }
            }
        }
    }
}