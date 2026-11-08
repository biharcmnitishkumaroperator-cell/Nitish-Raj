package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.AssistantState
import com.example.model.OrbCustomization
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun VoiceOrb(
    modifier: Modifier = Modifier,
    size: Dp = 240.dp,
    state: AssistantState = AssistantState.IDLE,
    audioAmplitude: Float = 0f,
    customization: OrbCustomization = OrbCustomization(),
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_animations")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.93f,
        targetValue = 1.07f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (1400 / customization.pulseSpeed).toInt().coerceAtLeast(400),
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (state == AssistantState.THINKING) 3000 else 10000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbital_rotation"
    )

    val counterRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (state == AssistantState.THINKING) 2500 else 8500,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "counter_rotation"
    )

    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_offset"
    )

    val preset = customization.preset
    val primaryColor = preset.primaryColor
    val secondaryColor = preset.secondaryColor
    val auraColor = preset.auraColor

    Box(
        modifier = modifier
            .size(size)
            .testTag("voice_orb_container")
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val baseRadius = (this.size.minDimension / 2.7f)

            // Dynamic reactivity boost when listening or speaking
            val dynamicBoost = when (state) {
                AssistantState.LISTENING -> audioAmplitude * 28.dp.toPx()
                AssistantState.SPEAKING -> (sin(waveOffset) * 0.5f + 0.5f) * 16.dp.toPx()
                AssistantState.THINKING -> 8.dp.toPx()
                else -> 0f
            }

            val currentRadius = (baseRadius * pulseScale + dynamicBoost)
                .coerceAtMost(this.size.minDimension / 2f - 12.dp.toPx())

            // 1. Outermost Aura Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        auraColor.copy(alpha = 0.5f * customization.coreGlowIntensity),
                        auraColor.copy(alpha = 0.15f * customization.coreGlowIntensity),
                        Color.Transparent
                    ),
                    center = center,
                    radius = currentRadius * 1.55f
                ),
                radius = currentRadius * 1.55f,
                center = center
            )

            // 2. Inner Radiant Core Sphere
            val coreBrush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.95f),
                    primaryColor.copy(alpha = 0.9f),
                    secondaryColor.copy(alpha = 0.85f),
                    primaryColor.copy(alpha = 0.2f)
                ),
                center = Offset(center.x - currentRadius * 0.25f, center.y - currentRadius * 0.25f),
                radius = currentRadius
            )
            drawCircle(brush = coreBrush, radius = currentRadius, center = center)

            // 3. Multi-ring Orbital Energy Arcs
            val ringCount = customization.ringCount.coerceIn(1, 5)
            for (i in 0 until ringCount) {
                val ringRadius = currentRadius + (i + 1) * 11.dp.toPx()
                val isEven = i % 2 == 0
                val angle = if (isEven) rotationAngle * (1f + i * 0.3f) else counterRotation * (1f + i * 0.2f)

                rotate(degrees = angle, pivot = center) {
                    // Two dashed glowing arcs
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                primaryColor.copy(alpha = 0.1f),
                                primaryColor.copy(alpha = 0.9f),
                                secondaryColor.copy(alpha = 0.9f),
                                Color.Transparent
                            ),
                            center = center
                        ),
                        startAngle = 30f + i * 20f,
                        sweepAngle = 140f,
                        useCenter = false,
                        style = Stroke(
                            width = (2.2f - i * 0.3f).dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    )

                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                secondaryColor.copy(alpha = 0.1f),
                                secondaryColor.copy(alpha = 0.85f),
                                primaryColor.copy(alpha = 0.7f),
                                Color.Transparent
                            ),
                            center = center
                        ),
                        startAngle = 210f + i * 20f,
                        sweepAngle = 110f,
                        useCenter = false,
                        style = Stroke(
                            width = (1.8f - i * 0.2f).dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    )
                }
            }

            // 4. Reactive Audio Spectrum Ring around core (when listening / speaking)
            if (customization.reactiveSoundWaves && (state == AssistantState.LISTENING || state == AssistantState.SPEAKING)) {
                val pointCount = 36
                val path = Path()
                for (step in 0 until pointCount) {
                    val angle = (step.toFloat() / pointCount) * 2f * Math.PI.toFloat()
                    val waveAmp = if (state == AssistantState.LISTENING) {
                        sin(angle * 6 + waveOffset) * (audioAmplitude * 20.dp.toPx() + 4.dp.toPx())
                    } else {
                        cos(angle * 4 + waveOffset) * 12.dp.toPx()
                    }
                    val r = currentRadius + 4.dp.toPx() + waveAmp
                    val x = center.x + r * cos(angle).toFloat()
                    val y = center.y + r * sin(angle).toFloat()
                    if (step == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }
                path.close()
                drawPath(
                    path = path,
                    color = primaryColor.copy(alpha = 0.75f),
                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // 5. Center Core Specular Sheen
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.5f),
                        Color.Transparent
                    ),
                    center = Offset(center.x - currentRadius * 0.3f, center.y - currentRadius * 0.35f),
                    radius = currentRadius * 0.45f
                ),
                radius = currentRadius * 0.45f,
                center = Offset(center.x - currentRadius * 0.3f, center.y - currentRadius * 0.35f)
            )
        }
    }
}
