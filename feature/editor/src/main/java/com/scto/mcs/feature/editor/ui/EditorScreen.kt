package com.scto.mcs.feature.editor.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

import com.scto.mcs.feature.editor.EditorViewModel
import com.scto.mcs.feature.settings.SettingsViewModel
import com.scto.mcs.feature.terminal.ui.TerminalScreen
import com.scto.mcs.feature.terminal.TerminalViewModel
import com.scto.mcs.core.ui.components.sidepanel.filetree.FileTreeViewModel

import kotlinx.coroutines.launch

/**
 * Editoransicht mit integriertem Terminal als BottomSheet.
 * Erlaubt gleichzeitiges Arbeiten am Code und Überwachen der Build-Prozesse.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    settingsViewModel: SettingsViewModel,
    editorViewModel: EditorViewModel,
    fileTreeViewModel: FileTreeViewModel = hiltViewModel(),
    terminalViewModel: TerminalViewModel = hiltViewModel()
) {
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded
        )
    )
    val scope = rememberCoroutineScope()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 48.dp, // Nur der Header/Handle ist sichtbar wenn minimiert
        sheetDragHandle = null, // Wir nutzen unseren eigenen Handle in TerminalScreen
        sheetContent = {
            // Terminal Bereich im BottomSheet
            Box(modifier = Modifier.fillMaxHeight(0.5f)) {
                TerminalScreen(
                    viewModel = terminalViewModel,
                    workingDir = null // Hier könnte das aktuelle Projekt-Verzeichnis übergeben werden
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (editorViewModel.openFiles.isNotEmpty()) {
                // Tab-Leiste für Dateien
                EditorTabs(editorViewModel)

                // Sora Editor Bereich
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    editorViewModel.activeFile?.let { file ->
                        EditorView(
                            editorFile = file,
                            viewModel = editorViewModel,
                            lspClient = null // Wird später injiziert
                        )
                    }
                }
            } else {
                // Willkommens-Screen
                EmptyEditorState(onExpandTerminal = {
                    scope.launch { scaffoldState.bottomSheetState.expand() }
                })
            }
        }
    }
}

@Composable
private fun EditorTabs(viewModel: EditorViewModel) {
    ScrollableTabRow(
        selectedTabIndex = viewModel.activeFileIndex,
        edgePadding = 0.dp,
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
        divider = {}
    ) {
        viewModel.openFiles.forEachIndexed { index, file ->
            Tab(
                selected = viewModel.activeFileIndex == index,
                onClick = { viewModel.activeFileIndex = index }
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = file.name,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (viewModel.activeFileIndex == index) 
                            MaterialTheme.colorScheme.primary 
                        else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = { viewModel.closeTab(index) },
                        modifier = Modifier.size(16.dp)
                    ) {
                        Icon(Icons.Default.Close, null, modifier = Modifier.size(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyEditorState(onExpandTerminal: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.CodeOff,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.outlineVariant
            )
            Text(
                "Kein aktives Dokument",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Öffne eine Datei im Explorer oder nutze das Terminal für Befehle.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 48.dp)
            )
            Spacer(Modifier.height(24.dp))
            OutlinedButton(onClick = onExpandTerminal) {
                Icon(Icons.Default.Terminal, null)
                Spacer(Modifier.width(8.dp))
                Text("Terminal öffnen")
            }
        }
    }
}
