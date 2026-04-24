package com.scto.mcs.feature.git

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scto.mcs.core.domain.model.*
import com.scto.mcs.core.domain.repository.GitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GitViewModel @Inject constructor(
    private val gitRepository: GitRepository
) : ViewModel() {

    private val _repoPath = MutableStateFlow("")
    val repoPath = _repoPath.asStateFlow()

    private val _status = MutableStateFlow<GitStatus?>(null)
    val status = _status.asStateFlow()

    private val _history = MutableStateFlow<List<GitCommit>>(emptyList())
    val history = _history.asStateFlow()

    private val _branches = MutableStateFlow<List<GitBranch>>(emptyList())
    val branches = _branches.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun loadRepo(path: String) {
        _repoPath.value = path
        refresh()
    }

    fun refresh() {
        val path = _repoPath.value
        if (path.isEmpty()) return
        viewModelScope.launch {
            _isLoading.value = true
            gitRepository.getStatus(path).onSuccess { _status.value = it }
            gitRepository.getHistory(path).onSuccess { _history.value = it }
            gitRepository.getBranches(path).onSuccess { _branches.value = it }
            _isLoading.value = false
        }
    }

    fun checkoutBranch(branchName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            gitRepository.checkout(_repoPath.value, branchName).onSuccess { refresh() }
            _isLoading.value = false
        }
    }

    fun createBranch(branchName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            gitRepository.checkout(_repoPath.value, branchName, createNew = true).onSuccess { refresh() }
            _isLoading.value = false
        }
    }

    fun stageFile(file: String) {
        viewModelScope.launch {
            gitRepository.stageFiles(_repoPath.value, listOf(file)).onSuccess { refresh() }
        }
    }

    fun unstageFile(file: String) {
        viewModelScope.launch {
            gitRepository.unstageFiles(_repoPath.value, listOf(file)).onSuccess { refresh() }
        }
    }

    fun commit(message: String) {
        if (message.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            gitRepository.commit(_repoPath.value, message).onSuccess { refresh() }
            _isLoading.value = false
        }
    }
}