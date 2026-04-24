package com.scto.mcs.feature.editor

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scto.mcs.core.build_tools.indexing.api.ProjectIndexer
import com.scto.mcs.core.build_tools.lsp.models.Position
import com.scto.mcs.core.editor.lsp.LspClient
import com.scto.mcs.core.domain.repository.GitRepository
import com.scto.mcs.core.domain.repository.LineDiff
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val indexer: ProjectIndexer,
    private val gitRepository: GitRepository
) : ViewModel() {

    private val _openFiles = mutableStateListOf<EditorFile>()
    val openFiles: List<EditorFile> get() = _openFiles

    var activeFileIndex by mutableStateOf(0)
    val activeFile: EditorFile? get() = _openFiles.getOrNull(activeFileIndex)

    // Git Diff Status für die aktive Datei
    private val _activeFileDiff = MutableStateFlow<List<LineDiff>>(emptyList())
    val activeFileDiff: StateFlow<List<LineDiff>> = _activeFileDiff

    private val _pendingScrollToLine = MutableSharedFlow<Int>()
    val pendingScrollToLine: SharedFlow<Int> = _pendingScrollToLine

    /**
     * Aktualisiert den Git-Diff für die aktuelle Datei.
     */
    fun refreshGitDiff(repoPath: String) {
        val file = activeFile ?: return
        val relativePath = file.uri.path?.substringAfter(repoPath)?.removePrefix("/") ?: return

        viewModelScope.launch {
            gitRepository.getDiffForFile(repoPath, relativePath).onSuccess { diffs ->
                _activeFileDiff.value = diffs
            }
        }
    }

    fun goToDefinition(lspClient: LspClient?) {
        val file = activeFile ?: return
        val editor = file.editorInstance ?: return
        viewModelScope.launch {
            val locations = lspClient?.requestDefinition(file.uri.toString(), Position(editor.cursor.line, editor.cursor.column)) ?: emptyList()
            if (locations.isNotEmpty()) {
                val target = locations.first()
                jumpToLocation(Uri.parse(target.uri), target.range.start.line)
            }
        }
    }

    fun jumpToLocation(uri: Uri, line: Int) {
        val index = _openFiles.indexOfFirst { it.uri == uri }
        if (index != -1) {
            activeFileIndex = index
            viewModelScope.launch { _pendingScrollToLine.emit(line) }
        } else {
            openFileFromUri(uri)
            viewModelScope.launch {
                kotlinx.coroutines.delay(300)
                _pendingScrollToLine.emit(line)
            }
        }
    }

    fun openFileFromUri(uri: Uri) {
        val existingIndex = _openFiles.indexOfFirst { it.uri == uri }
        if (existingIndex != -1) {
            activeFileIndex = existingIndex
            return
        }
        viewModelScope.launch {
            val content = context.contentResolver.openInputStream(uri)?.use { it.bufferedReader().readText() } ?: ""
            _openFiles.add(EditorFile(uri, uri.lastPathSegment ?: "Unbenannt", mutableStateOf(content)))
            activeFileIndex = _openFiles.size - 1
            // Trigger Diff after open
            // refreshGitDiff(currentRepoPath) 
        }
    }

    fun closeTab(index: Int) {
        if (index in _openFiles.indices) {
            _openFiles.removeAt(index)
            activeFileIndex = activeFileIndex.coerceIn(0, (_openFiles.size - 1).coerceAtLeast(0))
        }
    }
    
    fun onContentChanged(newContent: String) {
        activeFile?.let {
            if (it.content.value != newContent) {
                it.content.value = newContent
                // Optional: Diff bei jeder Änderung re-triggern (CPU intensiv)
            }
        }
    }

    fun clearDocumentation() {}
    val currentDoc: StateFlow<com.scto.mcs.core.domain.model.Documentation?> = MutableStateFlow(null)
}

data class EditorFile(
    val uri: Uri,
    val name: String,
    val content: MutableState<String>,
    var editorInstance: io.github.rosemoe.sora.widget.CodeEditor? = null
)