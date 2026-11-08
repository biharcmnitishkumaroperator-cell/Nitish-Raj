package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.ClaudePeach
import com.example.ui.theme.GeminiPurple
import com.example.ui.theme.GroqOrange
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.PerplexityCyan

enum class AIModelProvider(
    val id: String,
    val displayName: String,
    val defaultModel: String,
    val tagline: String,
    val badgeColor: Color,
    val latencyLabel: String,
    val apiKeyPlaceholder: String
) {
    GEMINI(
        id = "gemini",
        displayName = "Google Gemini",
        defaultModel = "gemini-2.5-flash",
        tagline = "Multimodal reasoning & device intelligence",
        badgeColor = GeminiPurple,
        latencyLabel = "Ultra Fast (~150ms)",
        apiKeyPlaceholder = "AIzaSy..."
    ),
    GROQ(
        id = "groq",
        displayName = "Groq LPU",
        defaultModel = "llama-3.3-70b-versatile",
        tagline = "Blazing fast speech-to-thought inference",
        badgeColor = GroqOrange,
        latencyLabel = "Instant (~80ms)",
        apiKeyPlaceholder = "gsk_..."
    ),
    OPENAI(
        id = "openai",
        displayName = "OpenAI",
        defaultModel = "gpt-4o",
        tagline = "Omni multimodal & natural conversation",
        badgeColor = NeonCyan,
        latencyLabel = "Fast (~250ms)",
        apiKeyPlaceholder = "sk-proj-..."
    ),
    CLAUDE(
        id = "claude",
        displayName = "Anthropic Claude",
        defaultModel = "claude-3-5-sonnet-20241022",
        tagline = "Nuanced, creative & deeply helpful reasoning",
        badgeColor = ClaudePeach,
        latencyLabel = "Balanced (~300ms)",
        apiKeyPlaceholder = "sk-ant-..."
    ),
    PERPLEXITY(
        id = "perplexity",
        displayName = "Perplexity Sonar",
        defaultModel = "sonar-pro",
        tagline = "Live web search & cited answers",
        badgeColor = PerplexityCyan,
        latencyLabel = "Search Aware (~350ms)",
        apiKeyPlaceholder = "pplx-..."
    )
}

data class ModelConfig(
    val selectedProvider: AIModelProvider = AIModelProvider.GEMINI,
    val apiKeys: Map<String, String> = emptyMap(),
    val temperature: Float = 0.7f,
    val voicePersonality: String = "Cybernetic & Proactive",
    val codeninjavikAccount: String = "vikram@codeninjavik.in",
    val isCodeninjavikSynced: Boolean = true
)
