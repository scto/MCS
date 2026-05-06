package com.scto.mcs.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import com.scto.mcs.feature.welcome.ThemeColor

// --- Theme Vorschau-Karte ---
@Composable
fun ThemePreviewCard(
    theme: ThemeColor,
    isSelected: Boolean,
    isDarkTheme: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )
    val borderWidth by animateDpAsState(if (isSelected) 3.dp else 0.dp, label = "borderW")

    val targetSpec = if (isDarkTheme) theme.dark else theme.light

    val animBgColor by animateColorAsState(targetSpec.background, tween(500), label = "bgColor")
    val animPrimaryColor by animateColorAsState(targetSpec.primary, tween(500), label = "primColor")
    val animSurfaceColor by animateColorAsState(targetSpec.surface, tween(500), label = "surfColor")
    val animBorderColor by animateColorAsState(targetSpec.primary, tween(500), label = "borderColor")

    val circleOffsetOne by animateDpAsState(
        targetValue = if (isDarkTheme) 12.dp else 8.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "offset1"
    )
    val circleOffsetTwo by animateDpAsState(
        targetValue = if (isDarkTheme) 12.dp else 18.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "offset2"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.scale(scale)
    ) {
        Surface(
            onClick = onClick,
            modifier = Modifier.size(80.dp, 100.dp),
            shape = RoundedCornerShape(20.dp),
            color = animBgColor,
            border = BorderStroke(borderWidth, animBorderColor),
            shadowElevation = if (isSelected) 8.dp else 2.dp
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Oberer Ball (Primary)
                Box(
                    modifier = Modifier
                        .offset(x = circleOffsetOne, y = -circleOffsetOne)
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(animPrimaryColor.copy(alpha = 0.8f))
                )

                // Unterer Ball (Surface/Accent)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = circleOffsetTwo, y = circleOffsetTwo)
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(animSurfaceColor)
                )

                // Haken bei Auswahl
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (isDarkTheme) Color.Black else Color.White)
                            .border(1.dp, animPrimaryColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            null,
                            tint = animPrimaryColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = theme.name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}