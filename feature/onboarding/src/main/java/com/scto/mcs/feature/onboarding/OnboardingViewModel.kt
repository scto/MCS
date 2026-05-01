package com.scto.mcs.feature.onboarding

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state

    fun nextPage() {
        _state.value = _state.value.copy(currentPage = _state.value.currentPage + 1)
    }

    fun setPermissionGranted(granted: Boolean) {
        _state.value = _state.value.copy(isPermissionGranted = granted)
    }
}
