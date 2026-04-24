package com.scto.mcs.core.ui.components.sidepanel.filetree

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scto.mcs.core.domain.model.FileItem
import com.scto.mcs.core.domain.model.FileTreeMode
import com.scto.mcs.core.domain.repository.FileRepository
import com.scto.mcs.core.domain.usecase.GetFileTreeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel zur Steuerung des Dateibaums.
 * Reagiert auf Änderungen des Pfads und des Ansichtsmodus.
 */
@HiltViewModel
class FileTreeViewModel @Inject constructor(
    private val repository: FileRepository,
    private val getFileTreeUseCase: GetFileTreeUseCase
) : ViewModel() {

    private val _currentPath = MutableStateFlow("/")
    private val _viewMode = MutableStateFlow(FileTreeMode.EXPLORER)
    
    val viewMode: StateFlow<FileTreeMode> = _viewMode.asStateFlow()

    /**
     * Kombiniert Pfad und Modus zu einem reaktiven Stream der anzuzeigenden Dateien.
     */
    val currentFiles: StateFlow<List<FileItem>> = combine(_currentPath, _viewMode) { path, mode ->
        path to mode
    }.flatMapLatest { (path, mode) ->
        getFileTreeUseCase(path, mode)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setViewMode(mode: FileTreeMode) {
        _viewMode.value = mode
    }

    fun setPath(path: String) {
        _currentPath.value = path
    }

    // --- Datei-Operationen ---

    fun createFile(name: String, isDirectory: Boolean) {
        viewModelScope.launch {
            repository.create(_currentPath.value, name, isDirectory)
        }
    }

    fun deleteFile(item: FileItem) {
        viewModelScope.launch {
            repository.delete(item.path)
        }
    }

    fun renameFile(item: FileItem, newName: String) {
        viewModelScope.launch {
            repository.rename(item.path, newName)
        }
    }

    // --- Clipboard Logik für Cut/Copy/Paste ---
    private var clipboardSourcePath: String? = null
    private var isCutOperation: Boolean = false

    fun prepareCopy(path: String) {
        clipboardSourcePath = path
        isCutOperation = false
    }

    fun prepareCut(path: String) {
        clipboardSourcePath = path
        isCutOperation = true
    }

    fun paste() {
        val source = clipboardSourcePath ?: return
        viewModelScope.launch {
            if (isCutOperation) {
                repository.move(source, _currentPath.value)
            } else {
                repository.copy(source, _currentPath.value)
            }
            clipboardSourcePath = null
        }
    }
}