package com.scto.mcs.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scto.mcs.core.domain.usecase.CloneRepositoryUseCase
import com.scto.mcs.core.utils.FileSystemUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class DashboardUiState(
    val isCloned: Boolean = false,
    val clonedProjectPath: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val cloneRepositoryUseCase: CloneRepositoryUseCase,
    private val fileSystemUtils: FileSystemUtils
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState

    fun cloneRepository(url: String) {
        viewModelScope.launch {
            val projectName = url.substringAfterLast("/").removeSuffix(".git")
            val destination = File(fileSystemUtils.getMcsDirectory(), "projects/$projectName")
            val result = cloneRepositoryUseCase(url, destination)
            
            if (result.isSuccess) {
                _uiState.value = DashboardUiState(isCloned = true, clonedProjectPath = destination.absolutePath)
            }
        }
    }

    fun resetCloneState() {
        _uiState.value = DashboardUiState()
    }
}
