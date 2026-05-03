package com.scto.mcs.core.onboarding.state

import androidx.compose.ui.graphics.Color

/**
 * Modell für eine einzelne Onboarding-Seite.
 */
data class OnboardingPage(
    val title: String,
    val description: String,
    val imageSource: Any, // Erlaubt Int (ResId), String (URL), oder Uri
    val backgroundColor: Color = Color.White
)