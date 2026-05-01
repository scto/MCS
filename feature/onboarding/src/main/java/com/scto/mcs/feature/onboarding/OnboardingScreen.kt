package com.scto.mcs.feature.onboarding

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onComplete: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()

    // Berechtigungsprüfung (Android 13+ benötigt andere Berechtigungen, hier vereinfacht)
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    }
    val storagePermissionState = rememberPermissionState(permission)

    LaunchedEffect(pagerState.currentPage) {
        viewModel.setCurrentPage(pagerState.currentPage)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> OnboardingPage("Willkommen", "Willkommen bei MCS. Wir helfen dir beim Setup.")
                1 -> {
                    Column {
                        OnboardingPage("Berechtigungen", "Wir benötigen Zugriff auf deine Dateien.")
                        if (!storagePermissionState.status.isGranted) {
                            Button(onClick = { storagePermissionState.launchPermissionRequest() }) {
                                Text("Berechtigung erteilen")
                            }
                        } else {
                            Text("Berechtigung erteilt!")
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                if (pagerState.currentPage < 1) {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                } else {
                    viewModel.completeOnboarding()
                    onComplete()
                }
            },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text(if (pagerState.currentPage < 1) "Weiter" else "Starten")
        }
    }
}
