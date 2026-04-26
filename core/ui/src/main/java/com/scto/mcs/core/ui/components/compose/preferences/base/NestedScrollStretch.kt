package com.scto.mcs.core.ui.components.compose.preferences.base

/*
 * Copyright 2021, Lawnchair.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.view.animation.AnimationUtils
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Velocity
import kotlin.math.*

/**
 * Erzeugt einen elastischen "Stretch"-Effekt an den Rändern, ähnlich wie in Android 12+.
 * Diese Implementierung ist vollständig in Kotlin geschrieben und benötigt keine externen Java-Abhängigkeiten.
 */
@Composable
fun NestedScrollStretch(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val invalidateTick = remember { mutableIntStateOf(0) }
    val invalidate = { invalidateTick.intValue++ }

    // Physikalische Rechner für oben und unten
    val topStretch = remember { StretchCalculator(invalidate) }
    val bottomStretch = remember { StretchCalculator(invalidate) }

    val connection = remember {
        NestedScrollStretchConnection(topStretch, bottomStretch)
    }.apply { this.enabled = enabled }

    Box(
        modifier = modifier
            .nestedScroll(connection)
            .onSizeChanged {
                val h = it.height.toFloat()
                connection.containerHeight = h
                topStretch.setSize(h)
                bottomStretch.setSize(h)
            }
            .graphicsLayer {
                // Hier wird der visuelle Effekt angewendet
                invalidateTick.intValue // Observer für Re-Draws

                val topScale = topStretch.getScale()
                val bottomScale = bottomStretch.getScale()

                if (topScale > 1f) {
                    scaleY = topScale
                    pivotY = 0f
                } else if (bottomScale > 1f) {
                    scaleY = bottomScale
                    pivotY = size.height
                }
            }
    ) {
        content()
    }
}

/**
 * Interne Logik zur Berechnung der Dehnung basierend auf Scroll-Ereignissen.
 */
private class NestedScrollStretchConnection(
    val topStretch: StretchCalculator,
    val bottomStretch: StretchCalculator
) : NestedScrollConnection {

    var containerHeight = 0f
    var enabled = true

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        if (!enabled || source != NestedScrollSource.UserInput || containerHeight <= 0f) return Offset.Zero

        val availableY = available.y
        // Wenn bereits eine Dehnung aktiv ist, konsumiere den Scroll, um sie zu reduzieren
        return when {
            availableY < 0f && topStretch.getDistance() > 0f -> {
                val consumed = topStretch.onPull(availableY / containerHeight)
                Offset(0f, consumed * containerHeight)
            }
            availableY > 0f && bottomStretch.getDistance() > 0f -> {
                val consumed = bottomStretch.onPull(-availableY / containerHeight)
                Offset(0f, -consumed * containerHeight)
            }
            else -> Offset.Zero
        }
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        if (!enabled || source != NestedScrollSource.UserInput || containerHeight <= 0f) return Offset.Zero

        val availableY = available.y
        if (availableY > 0f) {
            topStretch.onPull(availableY / containerHeight)
        } else if (availableY < 0f) {
            bottomStretch.onPull(-availableY / containerHeight)
        }
        return Offset.Zero
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        topStretch.onRelease()
        bottomStretch.onRelease()
        return Velocity.Zero
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        if (!enabled || containerHeight <= 0f) return Velocity.Zero
        
        if (available.y > 0f) {
            topStretch.onAbsorb(available.y.toInt())
        } else if (available.y < 0f) {
            bottomStretch.onAbsorb(-available.y.toInt())
        }
        return Velocity.Zero
    }
}

/**
 * Simuliert die Feder-Physik für den Stretch-Effekt.
 */
class StretchCalculator(private val onUpdate: () -> Unit) {
    private var distance = 0f
    private var velocity = 0f
    private var startTime = 0L
    private var isFinished = true
    private var height = 1f

    private val naturalFrequency = 24.657
    private val dampingRatio = 0.98

    fun setSize(h: Float) { height = h }
    fun getDistance() = distance

    fun onPull(delta: Float): Float {
        isFinished = false
        startTime = AnimationUtils.currentAnimationTimeMillis()
        val oldDist = distance
        distance = (distance + delta).coerceIn(0f, 1f)
        velocity = 0f
        onUpdate()
        return distance - oldDist
    }

    fun onAbsorb(v: Int) {
        isFinished = false
        velocity = v * 0.15f // Anpassungsfaktor für Wurf-Geschwindigkeit
        startTime = AnimationUtils.currentAnimationTimeMillis()
        onUpdate()
    }

    fun onRelease() {
        if (!isFinished) {
            startTime = AnimationUtils.currentAnimationTimeMillis()
            onUpdate()
        }
    }

    fun getScale(): Float {
        if (isFinished) return 1f
        updateSpring()
        
        // Damp-Funktion für natürlicheres Aussehen
        val overscroll = abs(distance)
        val intensity = 0.016f * overscroll + 0.016f * (1 - exp(-overscroll * 8f))
        return 1f + intensity
    }

    private fun updateSpring() {
        val now = AnimationUtils.currentAnimationTimeMillis()
        val deltaT = (now - startTime) / 1000f
        if (deltaT < 0.001f) return
        startTime = now

        val mDampedFreq = naturalFrequency * sqrt(1 - dampingRatio * dampingRatio)
        val cosCoeff = distance * height
        val sinCoeff = (1 / mDampedFreq) * (dampingRatio * naturalFrequency * distance * height + velocity)
        
        val decay = exp(-dampingRatio * naturalFrequency * deltaT)
        val newDist = decay * (cosCoeff * cos(mDampedFreq * deltaT) + sinCoeff * sin(mDampedFreq * deltaT))
        
        distance = (newDist / height).toFloat()
        
        if (abs(distance * height) < 0.1f) {
            distance = 0f
            isFinished = true
        } else {
            onUpdate()
        }
    }
}