package com.srvhive.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.srvhive.app.editor.Editor
import com.srvhive.app.editor.LanguageManager
import com.srvhive.app.ui.components.FileTreeSidebar
import com.srvhive.app.ui.components.FileTreeViewModel
import io.github.rosemoe.sora.widget.EditorSearcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    settingsViewModel: SettingsViewModel,
    editorViewModel: EditorViewModel
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // Initialisierung des Dateibaum-ViewModels
    val fileTreeViewModel = remember { FileTreeViewModel() }
    var editorInstance by remember { mutableStateOf<Editor?>(null) }
    var isSearchVisible by remember { mutableStateOf(false) }

    // Picker zum Wählen eines Projekt-Ordners
    val folderPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        uri?.let {
            // In einer echten App müsste hier der physische Pfad aus dem URI extrahiert werden.
            // Für die Demo setzen wir einen internen Pfad:
            fileTreeViewModel.setProjectRoot(context.filesDir)
        }
    }

    val openPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { editorViewModel.openFileFromUri(context, it) }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                FileTreeSidebar(
                    viewModel = fileTreeViewModel,
                    onFileSelected = { file ->
                        editorViewModel.openFileFromUri(context, Uri.fromFile(file))
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("IDE Editor") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, "Projekt-Sidebar")
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearchVisible = !isSearchVisible }) {
                            Icon(Icons.Default.Search, "Suchen")
                        }
                        IconButton(onClick = { folderPicker.launch(null) }) {
                            Icon(Icons.Default.CreateNewFolder, "Ordner wählen")
                        }
                        
                        editorViewModel.activeFile?.let { activeFile ->
                            var showMenu by remember { mutableStateOf(false) }
                            Box {
                                IconButton(onClick = { showMenu = true }) {
                                    Icon(Icons.Default.MoreVert, "Optionen")
                                }
                                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                                    DropdownMenuItem(
                                        text = { Text("Speichern") },
                                        leadingIcon = { Icon(Icons.Default.Save, null) },
                                        onClick = {
                                            editorViewModel.saveCurrentFile(context)
                                            showMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                if (editorViewModel.openFiles.isNotEmpty()) {
                    // Tab-Leiste
                    ScrollableTabRow(
                        selectedTabIndex = editorViewModel.activeFileIndex,
                        edgePadding = 0.dp,
                        divider = {},
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        editorViewModel.openFiles.forEachIndexed { index, file ->
                            Tab(
                                selected = editorViewModel.activeFileIndex == index,
                                onClick = { editorViewModel.activeFileIndex = index }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (file.isDirty.value) {
                                        Icon(Icons.Default.Circle, null, modifier = Modifier.size(8.dp), tint = colorScheme.error)
                                        Spacer(Modifier.width(4.dp))
                                    }
                                    Text(file.name, style = MaterialTheme.typography.labelMedium)
                                    Spacer(Modifier.width(8.dp))
                                    IconButton(onClick = { editorViewModel.closeTab(index) }, modifier = Modifier.size(18.dp)) {
                                        Icon(Icons.Default.Close, null, modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                        }
                    }

                    // Such-Panel
                    AnimatedVisibility(visible = isSearchVisible) {
                        SearchReplacePanel(
                            editor = editorInstance,
                            onClose = { isSearchVisible = false }
                        )
                    }

                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        key(editorViewModel.activeFile?.uri) {
                            editorViewModel.activeFile?.let { activeFile ->
                                RosemoeEditorView(
                                    editorFile = activeFile,
                                    settingsViewModel = settingsViewModel,
                                    onEditorReady = { editorInstance = it }
                                )
                            }
                        }
                    }
                } else {
                    EmptyEditorPlaceholder(
                        onOpen = { openPicker.launch(arrayOf("*/*")) },
                        onOpenFolder = { scope.launch { drawerState.open() } }
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyEditorPlaceholder(onOpen: () -> Unit, onOpenFolder: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Code, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
            Text("Keine Datei aktiv", style = MaterialTheme.typography.titleMedium)
            Row(modifier = Modifier.padding(top = 16.dp)) {
                Button(onClick = onOpen) { Text("Datei öffnen") }
                Spacer(Modifier.width(8.dp))
                OutlinedButton(onClick = onOpenFolder) { Text("Sidebar") }
            }
        }
    }
}