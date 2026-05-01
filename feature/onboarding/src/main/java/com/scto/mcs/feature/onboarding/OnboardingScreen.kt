package com.scto.mcs.feature.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onComplete: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val storagePermissionState = rememberPermissionState(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    Column {
        when (state.currentPage) {
            0 -> OnboardingPage("Willkommen", "Dies ist das Onboarding.")
            1 -> {
                OnboardingPage("Berechtigungen", "Wir benötigen Speicherzugriff.")
                Button(onClick = { storagePermissionState.launchPermissionRequest() }) {
                    Text("Berechtigung erteilen")
                }
            }
        }
        
        Button(onClick = {
            if (state.currentPage < 1) {
                viewModel.nextPage()
            } else {
                onComplete()
            }
        }) {
            Text("Weiter")
        }
    }
}
