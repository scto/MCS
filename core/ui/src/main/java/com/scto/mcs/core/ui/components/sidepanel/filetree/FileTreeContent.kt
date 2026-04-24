package com.scto.mcs.core.ui.components.sidepanel.filetree

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scto.mcs.core.domain.model.FileItem
import com.scto.mcs.core.domain.model.FileTreeMode

/**
 * Hauptansicht des Dateibaums mit Tab-Navigation für verschiedene Ansichts-Absichten.
 */
@Composable
fun FileTreeContent(
    viewModel: FileTreeViewModel = hiltViewModel()
) {
    val files by viewModel.currentFiles.collectAsState()
    val activeMode by viewModel.viewMode.collectAsState()
    var selectedItem by remember { mutableStateOf<FileItem?>(null) }

    var showCreateDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(4.dp)) {
        // Kopfzeile
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Dateisystem", style = MaterialTheme.typography.titleSmall)
            IconButton(onClick = { /* Sortierung/Filterung */ }) {
                Icon(Icons.Default.FilterList, contentDescription = "Filtern", Modifier.size(20.dp))
            }
        }

        // Ansichts-Umschalter für Explorer, Modul und Package
        TabRow(
            selectedTabIndex = activeMode.ordinal,
            containerColor = Color.Transparent,
            divider = {},
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[activeMode.ordinal]),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            FileTreeMode.values().forEach { mode ->
                Tab(
                    selected = activeMode == mode,
                    onClick = { viewModel.setViewMode(mode) },
                    text = { 
                        Text(
                            text = when(mode) {
                                FileTreeMode.EXPLORER -> "Explorer"
                                FileTreeMode.MODUL -> "Modul"
                                FileTreeMode.PACKAGE -> "Package"
                            },
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                )
            }
        }

        // Toolbar für Schnellzugriffe
        FileActionToolbar(
            selectedItem = selectedItem,
            onNewFile = { showCreateDialog = true },
            onRename = { showRenameDialog = true },
            onDelete = { item -> viewModel.deleteFile(item); selectedItem = null },
            onCopy = { item -> viewModel.prepareCopy(item.path) },
            onCut = { item -> viewModel.prepareCut(item.path) },
            onPaste = { viewModel.paste() }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        // Die dynamische Dateiliste
        Box(modifier = Modifier.weight(1f)) {
            if (files.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Keine Inhalte", style = MaterialTheme.typography.bodySmall)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(files, key = { it.path }) { item ->
                        FileListItem(
                            item = item,
                            isSelected = selectedItem?.path == item.path,
                            onClick = { selectedItem = item }
                        )
                    }
                }
            }
        }
    }

    // Dialog-Handling
    if (showCreateDialog) {
        CreateFileDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name, isDir -> viewModel.createFile(name, isDir) }
        )
    }

    if (showRenameDialog && selectedItem != null) {
        RenameFileDialog(
            initialName = selectedItem!!.name,
            onDismiss = { showRenameDialog = false },
            onConfirm = { newName -> viewModel.renameFile(selectedItem!!, newName); selectedItem = null }
        )
    }
}

@Composable
private fun FileActionToolbar(
    selectedItem: FileItem?,
    onNewFile: () -> Unit,
    onRename: () -> Unit,
    onDelete: (FileItem) -> Unit,
    onCopy: (FileItem) -> Unit,
    onCut: (FileItem) -> Unit,
    onPaste: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        IconButton(onClick = onNewFile) { Icon(Icons.Default.Add, "Neu") }
        IconButton(onClick = onRename, enabled = selectedItem != null) { Icon(Icons.Default.Edit, "Umbenennen") }
        IconButton(onClick = { selectedItem?.let(onDelete) }, enabled = selectedItem != null) { Icon(Icons.Default.Delete, "Löschen") }
        IconButton(onClick = { selectedItem?.let(onCopy) }, enabled = selectedItem != null) { Icon(Icons.Default.ContentCopy, "Kopieren") }
        IconButton(onClick = { selectedItem?.let(onCut) }, enabled = selectedItem != null) { Icon(Icons.Default.ContentCut, "Ausschneiden") }
        IconButton(onClick = onPaste) { Icon(Icons.Default.ContentPaste, "Einfügen") }
    }
}

@Composable
private fun FileListItem(item: FileItem, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 6.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (item.isDirectory) Icons.Default.Folder else Icons.Default.InsertDriveFile,
            contentDescription = null,
            tint = if (item.isDirectory) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(item.name, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
    }
}