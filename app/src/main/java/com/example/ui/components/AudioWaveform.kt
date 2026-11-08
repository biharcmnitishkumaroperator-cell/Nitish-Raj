package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.AssistantState
import com.example.ui.theme.NeonCyan
import kotlin.math.sin

@Composable
fun AudioWaveform(
    modifier: Modifier = Modifier,
    state: AssistantState = AssistantState.IDLE,
    amplitude: Float = 0f,
    barCount: Int = 24,
    waveColor: Color = NeonCyan,
    height: Dp = 36.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform_anim")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(modifier = modifier.fillMaxWidth().height(height)) {
        val totalWidth = size.width
        val barWidth = 4.dp.toPx()
        val spacing = (totalWidth - (barCount * barWidth)) / (barCount - 1).coerceAtLeast(1)
        val centerY = size.height / 2f

        for (i in 0 until barCount) {
            val progress = i.toFloat() / barCount.toFloat()
            val dynamicHeight = when (state) {
                AssistantState.LISTENING -> {
                    val sineFactor = (sin(phase + progress * 6f) * 0.5f + 0.5f)
                    (size.height * (0.2f + (amplitude * 0.75f + sineFactor * 0.25f)))
                        .coerceIn(4.dp.toPx(), size.height)
                }
                AssistantState.SPEAKING -> {
                    val sineFactor = (sin(phase * 1.5f + progress * 8f) * 0.5f + 0.5f)
                    (size.height * (0.25f + sineFactor * 0.65f))
                        .coerceIn(4.dp.toPx(), size.height)
                }
                AssistantState.THINKING -> {
                    val pulse = (sin(phase * 2f + progress * 4f) * 0.5f + 0.5f)
                    (size.height * (0.15f + pulse * 0.35f))
                }
                else -> {
                    // Idle gentle shimmer
                    val idle = (sin(phase * 0.5f + progress * 3f) * 0.5f + 0.5f)
                    (size.height * (0.12f + idle * 0.12f))
                }
            }

            val x = i * (barWidth + spacing)
            val top = centerY - dynamicHeight / 2f

            drawRoundRect(
                color = waveColor.copy(alpha = if (state == AssistantState.IDLE) 0.35f else 0.85f),
                topLeft = Offset(x, top),
                size = Size(barWidth, dynamicHeight),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )
        }
    }
}
