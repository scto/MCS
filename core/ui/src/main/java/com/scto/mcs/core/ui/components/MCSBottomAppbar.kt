package com.scto.mcs.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
/**
 * Eine benutzerdefinierte BottomAppBar-Komponente für die Navigation zwischen Seiten.
 * Enthält Zurück- und Weiter-Buttons sowie eine Seitenanzeige (Pager Indicator).
 */
@Composable
fun MCSBottomAppBar(
    pagerState: PagerState,
    activeColor: Color,
    onBack: () -> Unit,
    onNext: () -> Unit,
    isLastPage: Boolean
) {
    // Abrufen der aktuellen Inhaltsfarbe für die Icons
    val iconColor = LocalContentColor.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Zurück-Button
        IconButton(
            onClick = onBack,
            enabled = pagerState.currentPage > 0,
            modifier = Modifier.size(56.dp)
        ) {
            if (pagerState.currentPage > 0) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Zurück",
                    tint = iconColor
                )
            }
        }

        // Seiten-Indikatoren (Dots)
        Row(verticalAlignment = Alignment.CenterVertically) {
            repeat(pagerState.pageCount) { iteration ->
                val isSelected = pagerState.currentPage == iteration
                
                // Animation für Breite und Farbe des Indikators
                val width by animateDpAsState(
                    targetValue = if (isSelected) 24.dp else 8.dp, 
                    label = "indicatorWidth"
                )
                val color by animateColorAsState(
                    targetValue = if (isSelected) activeColor else iconColor.copy(alpha = 0.3f),
                    label = "indicatorColor"
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

        // Weiter/Fertig-Button
        IconButton(
            onClick = onNext,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(
                imageVector = if (isLastPage) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = if (isLastPage) "Fertigstellen" else "Weiter",
                tint = activeColor
            )
        }
    }
}