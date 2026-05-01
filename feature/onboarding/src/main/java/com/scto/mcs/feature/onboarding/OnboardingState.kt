package com.scto.mcs.feature.onboarding

data class OnboardingState(
    val currentPage: Int = 0,
    val isPermissionGranted: Boolean = false
)
