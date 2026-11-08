package com.example.model

sealed interface ActionCommand {
    data class MakeCall(
        val contactName: String,
        val phoneNumber: String,
        val prompt: String
    ) : ActionCommand

    data class SendWhatsApp(
        val recipientName: String,
        val phoneNumber: String,
        val message: String,
        val prompt: String
    ) : ActionCommand

    data class SendSms(
        val recipientName: String,
        val phoneNumber: String,
        val message: String,
        val prompt: String
    ) : ActionCommand

    data class OpenApp(
        val appName: String,
        val packageName: String?,
        val targetAction: String?,
        val prompt: String
    ) : ActionCommand

    data class ReadScreen(
        val screenTitle: String,
        val extractedText: String,
        val summary: String,
        val actionableElements: List<String>
    ) : ActionCommand

    data class GeneralAnswer(
        val responseText: String,
        val modelUsed: String
    ) : ActionCommand
}

enum class AssistantState {
    IDLE,
    LISTENING,
    THINKING,
    SPEAKING,
    ACTION_READY
}
