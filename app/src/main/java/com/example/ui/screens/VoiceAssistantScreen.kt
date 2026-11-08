package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.model.AssistantState
import com.example.ui.MyraViewModel
import com.example.ui.components.ActionPreviewCard
import com.example.ui.components.AudioWaveform
import com.example.ui.components.VoiceOrb
import com.example.ui.theme.DarkSurface
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
fun VoiceAssistantScreen(
    viewModel: MyraViewModel,
    onNavigateToModels: () -> Unit = {},
    onNavigateToAccount: () -> Unit = {}
) {
    val context = LocalContext.current
    val assistantState by viewModel.assistantState.collectAsState()
    val statusText by viewModel.statusText.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val currentAction by viewModel.currentAction.collectAsState()
    val orbCustomization by viewModel.orbCustomization.collectAsState()
    val selectedProvider by viewModel.selectedProvider.collectAsState()
    val codeninjavikAccount by viewModel.codeninjavikAccount.collectAsState()

    val isListening by viewModel.voiceRecognitionManager.isListening.collectAsState()
    val liveTranscript by viewModel.voiceRecognitionManager.transcribedText.collectAsState()
    val audioAmplitude by viewModel.voiceRecognitionManager.rmsAmplitude.collectAsState()

    var showTextInput by remember { mutableStateOf(false) }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startListening()
        }
    }

    val requestMicAndListen = {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            viewModel.toggleListening()
        } else {
            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpace)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar with Model Pill & codeninjavik sync indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MYRA",
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(QuantumEmerald)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "VOICE ENGINE ACTIVE",
                            color = QuantumEmerald,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    // AI Model Badge
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable(onClick = onNavigateToModels)
                            .testTag("active_model_pill"),
                        color = DarkSurfaceCard,
                        border = BorderStroke(1.dp, selectedProvider.badgeColor.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "✦ ${selectedProvider.displayName.substringBefore(" ")}",
                                color = selectedProvider.badgeColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // codeninjavik.in Account Pill
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable(onClick = onNavigateToAccount)
                            .testTag("codeninjavik_sync_pill"),
                        color = DarkSurfaceCard,
                        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "codeninjavik.in",
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // The Glowing Voice Orb
            VoiceOrb(
                size = 230.dp,
                state = assistantState,
                audioAmplitude = audioAmplitude,
                customization = orbCustomization,
                onClick = { requestMicAndListen() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Soundwave Visualizer
            AudioWaveform(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(30.dp),
                state = assistantState,
                amplitude = audioAmplitude,
                waveColor = orbCustomization.preset.primaryColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            // State & Status Banner
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkSurfaceVariant.copy(alpha = 0.8f),
                border = BorderStroke(1.dp, orbCustomization.preset.primaryColor.copy(alpha = 0.3f))
            ) {
                Text(
                    text = statusText,
                    color = when (assistantState) {
                        AssistantState.LISTENING -> NeonCyan
                        AssistantState.THINKING -> ElectricViolet
                        AssistantState.SPEAKING -> QuantumEmerald
                        AssistantState.ACTION_READY -> orbCustomization.preset.primaryColor
                        AssistantState.IDLE -> TextSecondary
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
            }

            // Live Speech Transcript
            if (isListening && liveTranscript.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "\"$liveTranscript\"",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }

            // Active Action Preview Card (Calls, WhatsApp, SMS, Apps, Screen)
            if (currentAction != null) {
                Spacer(modifier = Modifier.height(16.dp))
                ActionPreviewCard(
                    action = currentAction,
                    onExecuteAction = { viewModel.executeCurrentAction() },
                    onDismiss = { viewModel.dismissCurrentAction() },
                    onSpeakSummary = { summary -> viewModel.speakText(summary) }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Voice Action Prompt Chips
            Text(
                text = "SUGGESTED VOICE ACTIONS",
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "📞 Call Mom",
                    "💬 WhatsApp Alex: I'm on my way",
                    "✉️ Text Dad: Dinner ready",
                    "📱 Open Spotify and search lofi",
                    "👁️ Read my screen",
                    "🚀 Launch Camera"
                ).forEach { prompt ->
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                val cleanQuery = prompt
                                    .replace("📞 ", "")
                                    .replace("💬 ", "")
                                    .replace("✉️ ", "")
                                    .replace("📱 ", "")
                                    .replace("👁️ ", "")
                                    .replace("🚀 ", "")
                                viewModel.processUserQuery(cleanQuery)
                            }
                            .testTag("prompt_chip_${prompt.take(6)}"),
                        color = DarkSurfaceCard,
                        border = BorderStroke(1.dp, Color(0xFF233048))
                    ) {
                        Text(
                            text = prompt,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Text Input Drawer or Toggle
            AnimatedVisibility(visible = showTextInput) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { viewModel.onInputTextChanged(it) },
                        placeholder = { Text("Ask out loud or type query...", color = TextMuted, fontSize = 14.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("text_query_input"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color(0xFF2A3A55),
                            focusedContainerColor = DarkSurfaceCard,
                            unfocusedContainerColor = DarkSurfaceCard,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        trailingIcon = {
                            IconButton(
                                onClick = { viewModel.submitTextQuery() },
                                modifier = Modifier.testTag("submit_text_query_button")
                            ) {
                                Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = NeonCyan)
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { viewModel.submitTextQuery() })
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // Big Glowing Mic Control Hub
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Keyboard toggle button
                IconButton(
                    onClick = { showTextInput = !showTextInput },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceCard)
                        .testTag("keyboard_toggle_button")
                ) {
                    Text(text = "⌨️", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.width(24.dp))

                // Primary Voice Orb Mic Button
                val infiniteTransition = rememberInfiniteTransition(label = "mic_glow")
                val micScale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = if (isListening) 1.15f else 1.03f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(800, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "mic_pulse"
                )

                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .scale(micScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    if (isListening) NeonCyan else orbCustomization.preset.primaryColor,
                                    if (isListening) ElectricViolet else orbCustomization.preset.secondaryColor
                                )
                            )
                        )
                        .border(
                            2.dp,
                            if (isListening) Color.White else orbCustomization.preset.primaryColor.copy(alpha = 0.5f),
                            CircleShape
                        )
                        .clickable { requestMicAndListen() }
                        .testTag("primary_mic_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = if (isListening) "Stop Listening" else "Start Listening",
                        tint = DeepSpace,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.width(24.dp))

                // Stop speaking TTS button
                IconButton(
                    onClick = { viewModel.stopSpeaking() },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceCard)
                        .testTag("stop_speaking_button")
                ) {
                    Icon(imageVector = Icons.Default.Stop, contentDescription = "Stop Speaking", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
