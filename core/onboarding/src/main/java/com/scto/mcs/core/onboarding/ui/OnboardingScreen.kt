package com.scto.mcs.core.onboarding.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel

import com.scto.mcs.core.onboarding.state.OnboardingPage
import com.scto.mcs.core.onboarding.state.OnboardingViewModel

import kotlinx.coroutines.launch

/**
 * Das Haupt-Composable des Onboarding Moduls.
 * Es ist entkoppelt und benötigt nur die Initial-Daten und einen Finish-Callback.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    initialPages: List<OnboardingPage>,
    onOnboardingFinished: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    // Seiten initial setzen
    LaunchedEffect(initialPages) {
        viewModel.setInitialPages(initialPages)
    }

    // Navigation am Ende triggern
    LaunchedEffect(uiState.isCompleted) {
        if (uiState.isCompleted) {
            onOnboardingFinished()
        }
    }

    if (uiState.pages.isNotEmpty()) {
        val pagerState = rememberPagerState(pageCount = { uiState.pages.size })
        
        // Hintergrundfarbe animieren
        val currentBgColor = uiState.pages.getOrNull(pagerState.currentPage)?.backgroundColor ?: Color.White
        val animatedColor by animateColorAsState(
            targetValue = currentBgColor,
            animationSpec = tween(durationMillis = 600),
            label = "ColorTween"
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(animatedColor)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { index ->
                OnboardingPageContent(page = uiState.pages[index])
            }

            OnboardingBottomBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                pagerState = pagerState,
                onSkip = viewModel::onSkipClicked,
                onNext = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                },
                onDone = viewModel::onDoneClicked
            )
        }
    }
}