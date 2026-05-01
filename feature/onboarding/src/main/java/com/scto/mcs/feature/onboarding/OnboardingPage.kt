package com.scto.mcs.feature.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun OnboardingPage(title: String, description: String) {
    Column {
        Text(text = title)
        Text(text = description)
    }
}
