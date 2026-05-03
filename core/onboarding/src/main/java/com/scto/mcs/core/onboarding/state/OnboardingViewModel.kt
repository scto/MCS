package com.scto.mcs.core.onboarding.state

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    /**
     * Setzt die initiale Seitenliste.
     */
    fun setInitialPages(pages: List<OnboardingPage>) {
        if (_uiState.value.pages.isEmpty()) {
            _uiState.update { it.copy(pages = pages) }
        }
    }

    /**
     * Fügt eine Seite dynamisch hinzu (Thread-safe).
     */
    fun addPage(page: OnboardingPage) {
        _uiState.update { current ->
            current.copy(pages = current.pages + page)
        }
    }

    /**
     * Entfernt eine Seite per Index (Thread-safe).
     */
    fun removePage(index: Int) {
        _uiState.update { current ->
            val newList = current.pages.toMutableList()
            if (index in newList.indices) newList.removeAt(index)
            current.copy(pages = newList)
        }
    }

    fun onSkipClicked() {
        _uiState.update { it.copy(isCompleted = true) }
    }

    fun onDoneClicked() {
        _uiState.update { it.copy(isCompleted = true) }
    }
}