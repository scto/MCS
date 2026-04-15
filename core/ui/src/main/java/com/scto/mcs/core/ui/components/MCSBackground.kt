package com.scto.mcs.core.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun MCSBackground(
    currentTheme: ThemeColor?, // Angenommen, diese Klasse ist in deinem Projekt definiert
    isDarkTheme: Boolean,
    monetPrimary: Color? = null, // Monet-Modus Primärfarbe
    monetTertiary: Color? = null // Monet-Modus Akzentfarbe
) {
    val colorScheme = MaterialTheme.colorScheme
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    // Berechnung der Bildschirmmaße in Pixeln
    val screenWidth = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }

    // 1. Hintergrund-Basisfarbe bestimmen
    val baseBg = if (currentTheme != null) {
        if (isDarkTheme) currentTheme.dark.background else currentTheme.light.background
    } else {
        // Monet / System-Standard
        if (isDarkTheme) colorScheme.surface else colorScheme.surfaceContainerLowest
    }

    // 2. Farben für die animierten Lichtkugeln (Blobs) bestimmen
    val spec = if (isDarkTheme) currentTheme?.dark else currentTheme?.light
    val rawPrimary = spec?.primary ?: monetPrimary ?: colorScheme.primary
    val rawAccent = spec?.accent ?: monetTertiary ?: colorScheme.tertiary

    // 3. Sichtbarkeit der Lichtkugeln optimieren
    // In hellen Themes mischen wir die Farbe mit Grau, damit sie auf weißem Grund nicht "schmutzig" wirkt
    val blobAlpha = if (isDarkTheme) 0.15f else 0.12f
    val effectivePrimary = if (isDarkTheme) rawPrimary else rawPrimary.compositeOver(Color.Gray)
    val effectiveAccent = if (isDarkTheme) rawAccent else rawAccent.compositeOver(Color.Gray)

    // Farbübergangs-Animationen
    val animBg by animateColorAsState(baseBg, tween(600), label = "backgroundColor")
    val animPrimary by animateColorAsState(effectivePrimary, tween(600), label = "primaryBlobColor")
    val animAccent by animateColorAsState(effectiveAccent, tween(600), label = "accentBlobColor")

    // Unendliche Animation für die Bewegung der Kugeln
    val infiniteTransition = rememberInfiniteTransition(label = "backgroundMovement")

    val t1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time1"
    )
    
    val t2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time2"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animBg)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .blur(100.dp) // Weichzeichner für den "Glow"-Effekt
                .graphicsLayer { alpha = 1f }
        ) {
            // Berechnung der oszillierenden Positionen
            val offset1 = Offset(
                x = screenWidth * 0.5f + (screenWidth * 0.35f) * cos(t1),
                y = screenHeight * 0.4f + (screenHeight * 0.3f) * sin(t1)
            )
            val offset2 = Offset(
                x = screenWidth * 0.5f - (screenWidth * 0.35f) * cos(t2),
                y = screenHeight * 0.6f - (screenHeight * 0.3f) * sin(t2)
            )

            // Zeichnen der Lichtkugeln
            drawCircle(
                color = animPrimary.copy(alpha = blobAlpha),
                center = offset1,
                radius = screenWidth * 0.6f
            )
            drawCircle(
                color = animAccent.copy(alpha = blobAlpha),
                center = offset2,
                radius = screenWidth * 0.5f
            )
        }
    }
}