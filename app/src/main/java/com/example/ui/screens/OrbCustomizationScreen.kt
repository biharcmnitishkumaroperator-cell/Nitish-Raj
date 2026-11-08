package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AssistantState
import com.example.model.OrbPreset
import com.example.ui.MyraViewModel
import com.example.ui.components.VoiceOrb
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DeepSpace
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.QuantumEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun OrbCustomizationScreen(viewModel: MyraViewModel) {
    val context = LocalContext.current
    val customization by viewModel.orbCustomization.collectAsState()
    val speechRate by viewModel.repository.speechRate.collectAsState()
    val speechPitch by viewModel.repository.speechPitch.collectAsState()
    val autoSpeak by viewModel.repository.autoSpeak.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpace)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Section Header
        Text(
            text = "ORB & LAUNCHER STUDIO",
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )
        Text(
            text = "Customize the voice orb colors, pulse frequency, and launcher appearance",
            color = TextSecondary,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Live Interactive Preview Box
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("orb_preview_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
            border = BorderStroke(1.dp, customization.preset.primaryColor.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "LIVE ORB PREVIEW",
                    color = customization.preset.primaryColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                VoiceOrb(
                    size = 190.dp,
                    state = AssistantState.SPEAKING,
                    audioAmplitude = 0.65f,
                    customization = customization
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = customization.preset.title,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = customization.preset.description,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 1. Theme Presets
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.ColorLens, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "COLOR PRESETS",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OrbPreset.values().forEach { preset ->
                val isSelected = customization.preset == preset
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            viewModel.updateOrbCustomization(customization.copy(preset = preset))
                        }
                        .testTag("orb_preset_${preset.name}"),
                    color = if (isSelected) DarkSurfaceVariant else DarkSurfaceCard,
                    border = BorderStroke(1.dp, if (isSelected) preset.primaryColor else Color(0xFF223048))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Dual color dot
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(preset.primaryColor)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(preset.secondaryColor)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = preset.title,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = preset.description,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (isSelected) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "Selected", tint = preset.primaryColor)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Fine-tune Sliders
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = ElectricViolet, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "PHYSICS & LIGHT INTENSITY",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Core Glow Intensity
        Text(
            text = "Core Glow Intensity: ${(customization.coreGlowIntensity * 100).toInt()}%",
            color = TextSecondary,
            fontSize = 12.sp
        )
        Slider(
            value = customization.coreGlowIntensity,
            onValueChange = { viewModel.updateOrbCustomization(customization.copy(coreGlowIntensity = it)) },
            valueRange = 0.4f..1.0f,
            colors = SliderDefaults.colors(
                thumbColor = customization.preset.primaryColor,
                activeTrackColor = customization.preset.primaryColor
            ),
            modifier = Modifier.testTag("slider_glow_intensity")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Pulse Speed
        Text(
            text = "Pulse Speed: ${String.format("%.1f", customization.pulseSpeed)}x",
            color = TextSecondary,
            fontSize = 12.sp
        )
        Slider(
            value = customization.pulseSpeed,
            onValueChange = { viewModel.updateOrbCustomization(customization.copy(pulseSpeed = it)) },
            valueRange = 0.5f..2.0f,
            colors = SliderDefaults.colors(
                thumbColor = customization.preset.primaryColor,
                activeTrackColor = customization.preset.primaryColor
            ),
            modifier = Modifier.testTag("slider_pulse_speed")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Ring Count
        Text(
            text = "Orbital Energy Rings: ${customization.ringCount}",
            color = TextSecondary,
            fontSize = 12.sp
        )
        Slider(
            value = customization.ringCount.toFloat(),
            onValueChange = { viewModel.updateOrbCustomization(customization.copy(ringCount = it.toInt())) },
            valueRange = 1f..5f,
            steps = 3,
            colors = SliderDefaults.colors(
                thumbColor = customization.preset.primaryColor,
                activeTrackColor = customization.preset.primaryColor
            ),
            modifier = Modifier.testTag("slider_ring_count")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Reactive Soundwaves Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Reactive Soundwaves", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(text = "Ripples expand with live microphone amplitude", color = TextSecondary, fontSize = 11.sp)
            }
            Switch(
                checked = customization.reactiveSoundWaves,
                onCheckedChange = { viewModel.updateOrbCustomization(customization.copy(reactiveSoundWaves = it)) },
                colors = SwitchDefaults.colors(checkedThumbColor = customization.preset.primaryColor),
                modifier = Modifier.testTag("switch_reactive_soundwaves")
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Speech Synthesizer Voice Controls
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.RecordVoiceOver, contentDescription = null, tint = QuantumEmerald, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SPEECH SYNTHESIS ENGINE",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Speech Rate: ${String.format("%.2f", speechRate)}x",
            color = TextSecondary,
            fontSize = 12.sp
        )
        Slider(
            value = speechRate,
            onValueChange = {
                viewModel.repository.setSpeechSettings(it, speechPitch, autoSpeak)
            },
            valueRange = 0.8f..1.4f,
            colors = SliderDefaults.colors(thumbColor = QuantumEmerald, activeTrackColor = QuantumEmerald),
            modifier = Modifier.testTag("slider_speech_rate")
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Voice Pitch: ${String.format("%.2f", speechPitch)}x",
            color = TextSecondary,
            fontSize = 12.sp
        )
        Slider(
            value = speechPitch,
            onValueChange = {
                viewModel.repository.setSpeechSettings(speechRate, it, autoSpeak)
            },
            valueRange = 0.8f..1.3f,
            colors = SliderDefaults.colors(thumbColor = QuantumEmerald, activeTrackColor = QuantumEmerald),
            modifier = Modifier.testTag("slider_speech_pitch")
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Auto-speak Answers Out Loud", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(text = "Vocalize responses immediately on completion", color = TextSecondary, fontSize = 11.sp)
            }
            Switch(
                checked = autoSpeak,
                onCheckedChange = {
                    viewModel.repository.setSpeechSettings(speechRate, speechPitch, it)
                },
                colors = SwitchDefaults.colors(checkedThumbColor = QuantumEmerald),
                modifier = Modifier.testTag("switch_auto_speak")
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.speakText("Hello! This is MYRA speaking with your customized voice rate and pitch.")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .testTag("test_voice_sample_button"),
            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant, contentColor = TextPrimary),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF2E3E5E))
        ) {
            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = QuantumEmerald)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Test Spoken Voice Sample")
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
