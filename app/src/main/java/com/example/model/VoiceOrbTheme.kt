package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CyberPink
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.QuantumEmerald
import com.example.ui.theme.SolarAmber

enum class OrbPreset(
    val title: String,
    val description: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val auraColor: Color,
    val pulseFrequency: Float
) {
    CYBER_NEBULA(
        title = "Cyber Nebula",
        description = "Electric cyan with cosmic violet halos",
        primaryColor = NeonCyan,
        secondaryColor = ElectricViolet,
        auraColor = Color(0x3300E5FF),
        pulseFrequency = 1.0f
    ),
    SOLAR_FLARE(
        title = "Solar Flare",
        description = "Radiant amber & fiery solar corona",
        primaryColor = SolarAmber,
        secondaryColor = Color(0xFFEF4444),
        auraColor = Color(0x33F59E0B),
        pulseFrequency = 1.2f
    ),
    QUANTUM_EMERALD(
        title = "Quantum Emerald",
        description = "Bioluminescent green matrix energy",
        primaryColor = QuantumEmerald,
        secondaryColor = Color(0xFF06B6D4),
        auraColor = Color(0x3310B981),
        pulseFrequency = 0.9f
    ),
    SYNTHWAVE_PINK(
        title = "Synthwave Pink",
        description = "Retro neon magenta and deep purple",
        primaryColor = CyberPink,
        secondaryColor = ElectricViolet,
        auraColor = Color(0x33F43F5E),
        pulseFrequency = 1.1f
    ),
    LIQUID_CHROME(
        title = "Liquid Chrome",
        description = "Monochrome holographic silver shimmer",
        primaryColor = Color(0xFFE2E8F0),
        secondaryColor = Color(0xFF94A3B8),
        auraColor = Color(0x33CBD5E1),
        pulseFrequency = 0.8f
    )
}

data class OrbCustomization(
    val preset: OrbPreset = OrbPreset.CYBER_NEBULA,
    val coreGlowIntensity: Float = 0.85f,
    val pulseSpeed: Float = 1.0f,
    val ringCount: Int = 3,
    val reactiveSoundWaves: Boolean = true,
    val audioFeedbackHaptics: Boolean = true
)
