package com.scto.mcs.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scto.mcs.core.domain.usecase.CloneRepositoryUseCase
import com.scto.mcs.core.utils.FileSystemUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val cloneRepositoryUseCase: CloneRepositoryUseCase,
    private val fileSystemUtils: FileSystemUtils
) : ViewModel() {

    fun cloneRepository(url: String) {
        viewModelScope.launch {
            val projectName = url.substringAfterLast("/").removeSuffix(".git")
            val destination = File(fileSystemUtils.getMcsDirectory(), "projects/$projectName")
            cloneRepositoryUseCase(url, destination)
        }
    }
}
