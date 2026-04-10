package com.scto.mcs.feature.onboarding

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.scto.mcs.core.ui.theme.MCSTheme

@Composable
fun OnboardingScreen(
    onPermissionGranted: () -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                onPermissionGranted()
            }
        }
    }

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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = Uri.parse("package:${context.packageName}")
                    launcher.launch(intent)
                } else {
                    onPermissionGranted()
                }
            }) {
                Text("Berechtigung erteilen")
            }
        }
    }
}
