package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.ScreenSearchDesktop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ActionCommand
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DeepSpace
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.PhoneBlue
import com.example.ui.theme.QuantumEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhatsAppGreen

@Composable
fun ActionPreviewCard(
    modifier: Modifier = Modifier,
    action: ActionCommand?,
    onExecuteAction: () -> Unit,
    onDismiss: () -> Unit,
    onSpeakSummary: ((String) -> Unit)? = null
) {
    AnimatedVisibility(
        visible = action != null,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
        modifier = modifier
    ) {
        if (action == null) return@AnimatedVisibility

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("action_preview_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
            border = BorderStroke(1.dp, Brush.horizontalGradient(listOf(NeonCyan.copy(alpha = 0.5f), ElectricViolet.copy(alpha = 0.5f))))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Top header with icon and dismiss button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val (tag, color, icon) = when (action) {
                        is ActionCommand.MakeCall -> Triple("PHONE ACTION", PhoneBlue, Icons.Default.Call)
                        is ActionCommand.SendWhatsApp -> Triple("WHATSAPP ACTION", WhatsAppGreen, Icons.Default.Message)
                        is ActionCommand.SendSms -> Triple("SMS ACTION", NeonCyan, Icons.Default.Message)
                        is ActionCommand.OpenApp -> Triple("APP CONTROLLER", ElectricViolet, Icons.Default.Launch)
                        is ActionCommand.ReadScreen -> Triple("SCREEN CONTEXT", QuantumEmerald, Icons.Default.ScreenSearchDesktop)
                        is ActionCommand.GeneralAnswer -> Triple("MYRA RESPONSE", NeonCyan, Icons.Default.VolumeUp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(color.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = tag,
                            color = color,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("action_dismiss_button")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Dismiss", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Card body content based on action type
                when (action) {
                    is ActionCommand.MakeCall -> {
                        Text(
                            text = "Call ${action.contactName}",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = action.phoneNumber,
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onExecuteAction,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("execute_call_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = PhoneBlue, contentColor = DeepSpace),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Call Now via Dialer", fontWeight = FontWeight.Bold)
                        }
                    }

                    is ActionCommand.SendWhatsApp -> {
                        Text(
                            text = "To: ${action.recipientName} (${action.phoneNumber})",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurfaceVariant)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "\"${action.message}\"",
                                color = TextPrimary,
                                fontSize = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onExecuteAction,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("execute_whatsapp_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen, contentColor = DeepSpace),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Message, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Send via WhatsApp", fontWeight = FontWeight.Bold)
                        }
                    }

                    is ActionCommand.SendSms -> {
                        Text(
                            text = "To: ${action.recipientName} (${action.phoneNumber})",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurfaceVariant)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "\"${action.message}\"",
                                color = TextPrimary,
                                fontSize = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onExecuteAction,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("execute_sms_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = DeepSpace),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Message, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Send SMS Message", fontWeight = FontWeight.Bold)
                        }
                    }

                    is ActionCommand.OpenApp -> {
                        Text(
                            text = "Open ${action.appName}",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (action.targetAction != null) {
                            Text(
                                text = "Action: Search '${action.targetAction}'",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onExecuteAction,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("execute_app_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet, contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Launch, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Launch App", fontWeight = FontWeight.Bold)
                        }
                    }

                    is ActionCommand.ReadScreen -> {
                        Text(
                            text = action.screenTitle,
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = action.summary,
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                        if (action.actionableElements.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Detected Quick Actions:",
                                color = QuantumEmerald,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            action.actionableElements.take(3).forEach { act ->
                                Text(
                                    text = "• $act",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { onSpeakSummary?.invoke(action.summary) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("read_out_loud_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = QuantumEmerald, contentColor = DeepSpace),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Speak Summary", fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .weight(0.6f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Done", color = TextSecondary)
                            }
                        }
                    }

                    is ActionCommand.GeneralAnswer -> {
                        Text(
                            text = action.responseText,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Model: ${action.modelUsed}",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
