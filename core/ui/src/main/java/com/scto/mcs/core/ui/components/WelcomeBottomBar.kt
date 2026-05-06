package com.scto.mcs.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

import com.scto.mcs.core.resources.R

// --- Untere Navigationsleiste ---
@Composable
fun WelcomeBottomBar(
    pagerState: PagerState,
    activeColor: Color,
    onBack: () -> Unit,
    onNext: () -> Unit,
    isLastPage: Boolean
) {
    val iconColor = LocalContentColor.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onBack,
            enabled = pagerState.currentPage > 0,
            modifier = Modifier.size(56.dp)
        ) {
            if (pagerState.currentPage > 0) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.action_back),
                    tint = iconColor
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            repeat(pagerState.pageCount) { iteration ->
                val isSelected = pagerState.currentPage == iteration
                val width by animateDpAsState(if (isSelected) 24.dp else 8.dp, label = "w")
                val color by animateColorAsState(
                    if (isSelected) activeColor else iconColor.copy(alpha = 0.3f),
                    label = "c"
                )
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .height(6.dp)
                        .width(width)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }

        IconButton(
            onClick = onNext,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(
                imageVector = if (isLastPage) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = stringResource(R.string.action_next),
                tint = activeColor
            )
        }
    }
}