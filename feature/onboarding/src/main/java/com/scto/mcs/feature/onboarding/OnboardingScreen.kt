package com.scto.mcs.feature.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scto.mcs.core.ui.theme.MCSTheme

@Composable
fun OnboardingScreen(
    onPermissionGranted: () -> Unit
) {
    MCSTheme {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Willkommen bei MCS")
            Spacer(modifier = Modifier.height(16.dp))
            Text("Bitte gewähre Zugriff auf den Speicher, um Projekte zu verwalten.")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                onPermissionGranted()
            }) {
                Text("Berechtigung erteilen")
            }
        }
    }
}
