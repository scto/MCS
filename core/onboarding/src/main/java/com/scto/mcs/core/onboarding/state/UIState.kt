package com.scto.mcs.core.onboarding.state

/**
 * Zustand der Onboarding-Komponente.
 */
data class OnboardingUiState(
    val pages: List<OnboardingPage> = emptyList(),
    val isCompleted: Boolean = false
)