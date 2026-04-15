package com.scto.mcs.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Eine Vorschaukarte für vordefinierte Themes.
 * Beinhaltet Animationen für Skalierung, Farben und Positionen der Dekoelemente.
 */
@Composable
fun ThemePreviewCard(
    theme: ThemeColor,
    isSelected: Boolean,
    isDarkTheme: Boolean,
    onClick: () -> Unit
) {
    // 1. Animation für den Auswahlstatus (Skalierung)
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "selectionScale"
    )
    val borderWidth by animateDpAsState(
        targetValue = if (isSelected) 3.dp else 0.dp, 
        label = "borderWidth"
    )

    // 2. Farbübergänge basierend auf dem Modus (Hell/Dunkel)
    val targetSpec = if (isDarkTheme) theme.dark else theme.light

    val animBgColor by animateColorAsState(targetSpec.background, tween(500), label = "bgColor")
    val animPrimaryColor by animateColorAsState(targetSpec.primary, tween(500), label = "primColor")
    val animSurfaceColor by animateColorAsState(targetSpec.surface, tween(500), label = "surfColor")
    val animBorderColor by animateColorAsState(targetSpec.primary, tween(500), label = "borderColor")

    // 3. Positionsanimation der dekorativen Kreise
    val circleOffsetOne by animateDpAsState(
        targetValue = if (isDarkTheme) 12.dp else 8.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "circleOffset1"
    )
    val circleOffsetTwo by animateDpAsState(
        targetValue = if (isDarkTheme) 12.dp else 18.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "circleOffset2"
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
                // Dekorativer Kreis oben (Primary)
                Box(
                    modifier = Modifier
                        .offset(x = circleOffsetOne, y = -circleOffsetOne)
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(animPrimaryColor.copy(alpha = 0.8f))
                )

                // Dekorativer Kreis unten (Surface/Accent)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = circleOffsetTwo, y = circleOffsetTwo)
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(animSurfaceColor)
                )

                // Auswahl-Indikator (Häkchen)
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
                            imageVector = Icons.Default.Check,
                            contentDescription = "Ausgewählt",
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

/**
 * Eine Karte für die benutzerdefinierte Farbwahl.
 */
@Composable
internal fun CustomThemeCard(
    isSelected: Boolean, 
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f, 
        label = "customScale"
    )
    val borderWidth by animateDpAsState(
        targetValue = if (isSelected) 3.dp else 0.dp, 
        label = "customBorderWidth"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.scale(scale)
    ) {
        Surface(
            onClick = onClick,
            modifier = Modifier.size(80.dp, 100.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
            border = BorderStroke(borderWidth, MaterialTheme.colorScheme.primary),
            shadowElevation = if (isSelected) 6.dp else 0.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = "Benutzerdefiniert",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "自定义", // "Benutzerdefiniert"
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}