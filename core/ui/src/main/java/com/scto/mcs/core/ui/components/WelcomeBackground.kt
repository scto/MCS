package com.scto.mcs.core.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

import com.scto.mcs.feature.welcome.ThemeColor

import kotlin.math.cos
import kotlin.math.sin

// --- Hintergrund-Komponente ---
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun WelcomeBackground(
    currentTheme: ThemeColor?,
    isDarkTheme: Boolean,
    monetPrimary: Color? = null,
    monetTertiary: Color? = null
) {
    val colorScheme = MaterialTheme.colorScheme
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val screenWidth = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }

    // 1. Hintergrund-Grundfarbe ermitteln
    val baseBg = if (currentTheme != null) {
        if (isDarkTheme) currentTheme.dark.background else currentTheme.light.background
    } else {
        if (isDarkTheme) colorScheme.surface else colorScheme.surfaceContainerLowest
    }

    // 2. Lichtkugel-Farben ermitteln
    val spec = if (isDarkTheme) currentTheme?.dark else currentTheme?.light
    val rawPrimary = spec?.primary ?: monetPrimary ?: colorScheme.primary
    val rawAccent = spec?.accent ?: monetTertiary ?: colorScheme.tertiary

    // 3. Sichtbarkeit der Lichtkugeln im hellen Modus verbessern
    val blobAlpha = if (isDarkTheme) 0.15f else 0.12f

    val effectivePrimary = if (isDarkTheme) rawPrimary else rawPrimary.compositeOver(Color.Gray)
    val effectiveAccent = if (isDarkTheme) rawAccent else rawAccent.compositeOver(Color.Gray)

    // Animations-Übergänge
    val animBg by animateColorAsState(baseBg, tween(600), label = "bg")
    val animPrimary by animateColorAsState(effectivePrimary, tween(600), label = "prim")
    val animAccent by animateColorAsState(effectiveAccent, tween(600), label = "acc")

    val infiniteTransition = rememberInfiniteTransition(label = "bg_anim")

    val t1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(15000, easing = LinearEasing), RepeatMode.Restart), label = "t1"
    )
    val t2 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing), RepeatMode.Restart), label = "t2"
    )

    Box(modifier = Modifier.fillMaxSize().background(animBg)) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .blur(100.dp)
                .graphicsLayer { alpha = 1f }
        ) {
            val offset1 = Offset(
                x = screenWidth * 0.5f + (screenWidth * 0.35f) * cos(t1),
                y = screenHeight * 0.4f + (screenHeight * 0.3f) * sin(t1)
            )
            val offset2 = Offset(
                x = screenWidth * 0.5f - (screenWidth * 0.35f) * cos(t2),
                y = screenHeight * 0.6f - (screenHeight * 0.3f) * sin(t2)
            )

            drawCircle(color = animPrimary.copy(alpha = blobAlpha), center = offset1, radius = screenWidth * 0.6f)
            drawCircle(color = animAccent.copy(alpha = blobAlpha), center = offset2, radius = screenWidth * 0.5f)
        }
    }
}