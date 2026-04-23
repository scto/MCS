package com.srvhive.app.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.srvhive.app.ui.screens.FileSortBy
import com.srvhive.app.ui.screens.FileSortOrder
import com.srvhive.app.ui.screens.SettingsViewModel
import java.io.File

/**
 * ViewModel zur Verwaltung der Projektstruktur.
 */
class FileTreeViewModel {
    var rootFile by mutableStateOf<File?>(null)
    val expandedFolders = mutableStateMapOf<String, Boolean>()

    fun setProjectRoot(file: File) {
        rootFile = file
    }

    fun toggleFolder(path: String) {
        expandedFolders[path] = !(expandedFolders[path] ?: false)
    }
}

@Composable
fun FileTreeSidebar(
    viewModel: FileTreeViewModel,
    settingsViewModel: SettingsViewModel,
    onFileSelected: (File) -> Unit,
    modifier: Modifier = Modifier
) {
    var fileToRename by remember { mutableStateOf<File?>(null) }
    var fileToDelete by remember { mutableStateOf<File?>(null) }

    Column(modifier = modifier.fillMaxHeight().width(280.dp)) {
        Text(
            text = "PROJEKT",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(16.dp)
        )
        
        HorizontalDivider()

        val root = viewModel.rootFile
        if (root != null && root.exists()) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                renderDirectory(root, 0, viewModel, settingsViewModel, onFileSelected, this, 
                    onRename = { fileToRename = it }, 
                    onDelete = { fileToDelete = it }
                )
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Kein Verzeichnis gewählt", style = MaterialTheme.typography.bodySmall)
            }
        }
    }

    // Dialoge für Dateioperationen
    fileToRename?.let { file ->
        RenameDialog(file = file, onDismiss = { fileToRename = null }, onConfirm = { newName ->
            val dest = File(file.parentFile, newName)
            if (file.renameTo(dest)) fileToRename = null
        })
    }

    fileToDelete?.let { file ->
        AlertDialog(
            onDismissRequest = { fileToDelete = null },
            title = { Text("Löschen") },
            text = { Text("Möchtest du '${file.name}' wirklich löschen?") },
            confirmButton = {
                TextButton(onClick = { 
                    if (file.deleteRecursively()) fileToDelete = null 
                }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                    Text("Löschen")
                }
            },
            dismissButton = { TextButton(onClick = { fileToDelete = null }) { Text("Abbrechen") } }
        )
    }
}

private fun renderDirectory(
    directory: File,
    level: Int,
    viewModel: FileTreeViewModel,
    settings: SettingsViewModel,
    onFileSelected: (File) -> Unit,
    scope: androidx.compose.foundation.lazy.LazyListScope,
    onRename: (File) -> Unit,
    onDelete: (File) -> Unit
) {
    // Dateien filtern und sortieren basierend auf SettingsViewModel
    val rawFiles = directory.listFiles() ?: return
    
    val filteredFiles = if (settings.showHiddenFiles) {
        rawFiles
    } else {
        rawFiles.filter { !it.name.startsWith(".") }
    }

    val sortedFiles = when (settings.fileSortBy) {
        FileSortBy.NAME -> filteredFiles.sortedBy { it.name.lowercase() }
        FileSortBy.TYPE -> filteredFiles.sortedWith(compareBy({ !it.isDirectory }, { it.extension.lowercase() }, { it.name.lowercase() }))
        FileSortBy.SIZE -> filteredFiles.sortedBy { it.length() }
    }.let { 
        if (settings.fileSortOrder == FileSortOrder.DESCENDING) it.reversed() else it 
    }

    // Ordner immer oben anzeigen (optional, kann auch über Settings gesteuert werden)
    val finalFiles = sortedFiles.sortedByDescending { it.isDirectory }

    finalFiles.forEach { file ->
        val isExpanded = viewModel.expandedFolders[file.absolutePath] ?: false
        
        scope.item(key = file.absolutePath) {
            FileTreeItem(
                name = file.name,
                level = level,
                isFolder = file.isDirectory,
                isExpanded = isExpanded,
                onClick = {
                    if (file.isDirectory) viewModel.toggleFolder(file.absolutePath)
                    else onFileSelected(file)
                },
                onRename = { onRename(file) },
                onDelete = { onDelete(file) }
            )
        }

        if (file.isDirectory && isExpanded) {
            renderDirectory(file, level + 1, viewModel, settings, onFileSelected, scope, onRename, onDelete)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileTreeItem(
    name: String,
    level: Int,
    isFolder: Boolean,
    isExpanded: Boolean,
    onClick: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = { showMenu = true }
                )
                .padding(vertical = 6.dp, horizontal = 12.dp)
                .padding(start = (level * 16).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isFolder) {
                    if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.ChevronRight
                } else Icons.Default.Description,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = if (isFolder) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            )
            
            if (isFolder) {
                Icon(
                    Icons.Default.Folder, null,
                    modifier = Modifier.size(18.dp).padding(start = 4.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }

            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Kontextmenü
        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
            DropdownMenuItem(
                text = { Text("Umbenennen") },
                leadingIcon = { Icon(Icons.Default.Edit, null) },
                onClick = { showMenu = false; onRename() }
            )
            DropdownMenuItem(
                text = { Text("Löschen", color = MaterialTheme.colorScheme.error) },
                leadingIcon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) },
                onClick = { showMenu = false; onDelete() }
            )
        }
    }
}

@Composable
fun RenameDialog(file: File, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf(file.name) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Umbenennen") },
        text = { OutlinedTextField(value = text, onValueChange = { text = it }, singleLine = true) },
        confirmButton = { Button(onClick = { onConfirm(text) }) { Text("OK") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Abbrechen") } }
    )
}