package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AIModelProvider
import com.example.model.OrbCustomization
import com.example.model.OrbPreset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserPreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("myra_user_preferences", Context.MODE_PRIVATE)

    private val _orbCustomization = MutableStateFlow(loadOrbCustomization())
    val orbCustomization: StateFlow<OrbCustomization> = _orbCustomization.asStateFlow()

    private val _selectedProvider = MutableStateFlow(loadSelectedProvider())
    val selectedProvider: StateFlow<AIModelProvider> = _selectedProvider.asStateFlow()

    private val _codeninjavikAccount = MutableStateFlow(
        prefs.getString(KEY_CODENINJAVIK_ACCOUNT, "vikram@codeninjavik.in") ?: "vikram@codeninjavik.in"
    )
    val codeninjavikAccount: StateFlow<String> = _codeninjavikAccount.asStateFlow()

    private val _speechRate = MutableStateFlow(prefs.getFloat(KEY_SPEECH_RATE, 1.05f))
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    private val _speechPitch = MutableStateFlow(prefs.getFloat(KEY_SPEECH_PITCH, 1.0f))
    val speechPitch: StateFlow<Float> = _speechPitch.asStateFlow()

    private val _autoSpeak = MutableStateFlow(prefs.getBoolean(KEY_AUTO_SPEAK, true))
    val autoSpeak: StateFlow<Boolean> = _autoSpeak.asStateFlow()

    private fun loadOrbCustomization(): OrbCustomization {
        val presetName = prefs.getString(KEY_ORB_PRESET, OrbPreset.CYBER_NEBULA.name) ?: OrbPreset.CYBER_NEBULA.name
        val preset = try {
            OrbPreset.valueOf(presetName)
        } catch (e: Exception) {
            OrbPreset.CYBER_NEBULA
        }
        return OrbCustomization(
            preset = preset,
            coreGlowIntensity = prefs.getFloat(KEY_ORB_GLOW, 0.85f),
            pulseSpeed = prefs.getFloat(KEY_ORB_SPEED, 1.0f),
            ringCount = prefs.getInt(KEY_ORB_RINGS, 3),
            reactiveSoundWaves = prefs.getBoolean(KEY_ORB_REACTIVE, true),
            audioFeedbackHaptics = prefs.getBoolean(KEY_ORB_HAPTICS, true)
        )
    }

    private fun loadSelectedProvider(): AIModelProvider {
        val providerName = prefs.getString(KEY_SELECTED_PROVIDER, AIModelProvider.GEMINI.name) ?: AIModelProvider.GEMINI.name
        return try {
            AIModelProvider.valueOf(providerName)
        } catch (e: Exception) {
            AIModelProvider.GEMINI
        }
    }

    fun updateOrbCustomization(customization: OrbCustomization) {
        prefs.edit()
            .putString(KEY_ORB_PRESET, customization.preset.name)
            .putFloat(KEY_ORB_GLOW, customization.coreGlowIntensity)
            .putFloat(KEY_ORB_SPEED, customization.pulseSpeed)
            .putInt(KEY_ORB_RINGS, customization.ringCount)
            .putBoolean(KEY_ORB_REACTIVE, customization.reactiveSoundWaves)
            .putBoolean(KEY_ORB_HAPTICS, customization.audioFeedbackHaptics)
            .apply()
        _orbCustomization.value = customization
    }

    fun setSelectedProvider(provider: AIModelProvider) {
        prefs.edit().putString(KEY_SELECTED_PROVIDER, provider.name).apply()
        _selectedProvider.value = provider
    }

    fun getApiKey(provider: AIModelProvider): String {
        return prefs.getString(KEY_API_KEY_PREFIX + provider.id, "") ?: ""
    }

    fun setApiKey(provider: AIModelProvider, key: String) {
        prefs.edit().putString(KEY_API_KEY_PREFIX + provider.id, key).apply()
    }

    fun setCodeninjavikAccount(email: String) {
        prefs.edit().putString(KEY_CODENINJAVIK_ACCOUNT, email).apply()
        _codeninjavikAccount.value = email
    }

    fun setSpeechSettings(rate: Float, pitch: Float, autoSpeak: Boolean) {
        prefs.edit()
            .putFloat(KEY_SPEECH_RATE, rate)
            .putFloat(KEY_SPEECH_PITCH, pitch)
            .putBoolean(KEY_AUTO_SPEAK, autoSpeak)
            .apply()
        _speechRate.value = rate
        _speechPitch.value = pitch
        _autoSpeak.value = autoSpeak
    }

    companion object {
        private const val KEY_ORB_PRESET = "orb_preset"
        private const val KEY_ORB_GLOW = "orb_glow"
        private const val KEY_ORB_SPEED = "orb_speed"
        private const val KEY_ORB_RINGS = "orb_rings"
        private const val KEY_ORB_REACTIVE = "orb_reactive"
        private const val KEY_ORB_HAPTICS = "orb_haptics"
        private const val KEY_SELECTED_PROVIDER = "selected_provider"
        private const val KEY_API_KEY_PREFIX = "api_key_"
        private const val KEY_CODENINJAVIK_ACCOUNT = "codeninjavik_account"
        private const val KEY_SPEECH_RATE = "speech_rate"
        private const val KEY_SPEECH_PITCH = "speech_pitch"
        private const val KEY_AUTO_SPEAK = "auto_speak"
    }
}
