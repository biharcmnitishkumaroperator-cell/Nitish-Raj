package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MyraRepository
import com.example.data.UserPreferencesManager
import com.example.data.local.ContactEntity
import com.example.data.local.InteractionEntity
import com.example.data.local.MemoryEntity
import com.example.data.local.MyraDatabase
import com.example.model.AIModelProvider
import com.example.model.ActionCommand
import com.example.model.AssistantState
import com.example.model.OrbCustomization
import com.example.model.ScreenContentItem
import com.example.model.ScreenSampleData
import com.example.service.IntentActionDispatcher
import com.example.service.MyraBrainEngine
import com.example.service.TextToSpeechManager
import com.example.service.VoiceRecognitionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MyraViewModel(application: Application) : AndroidViewModel(application) {

    private val db = MyraDatabase.getDatabase(application)
    private val prefs = UserPreferencesManager(application)
    val repository = MyraRepository(db.myraDao(), prefs)

    val intentDispatcher = IntentActionDispatcher(application)
    val voiceRecognitionManager = VoiceRecognitionManager(application)
    val ttsManager = TextToSpeechManager(application)
    val brainEngine = MyraBrainEngine(repository, intentDispatcher)

    // UI States
    private val _assistantState = MutableStateFlow(AssistantState.IDLE)
    val assistantState: StateFlow<AssistantState> = _assistantState.asStateFlow()

    private val _currentAction = MutableStateFlow<ActionCommand?>(null)
    val currentAction: StateFlow<ActionCommand?> = _currentAction.asStateFlow()

    private val _statusText = MutableStateFlow("Tap the orb or mic to speak")
    val statusText: StateFlow<String> = _statusText.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    val orbCustomization: StateFlow<OrbCustomization> = repository.orbCustomization
    val selectedProvider: StateFlow<AIModelProvider> = repository.selectedProvider
    val codeninjavikAccount: StateFlow<String> = repository.codeninjavikAccount

    val memories: StateFlow<List<MemoryEntity>> = repository.getAllMemories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val contacts: StateFlow<List<ContactEntity>> = repository.getAllContacts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentHistory: StateFlow<List<InteractionEntity>> = repository.getRecentInteractions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val screenSamples: List<ScreenContentItem> = ScreenSampleData.samples

    init {
        // Wire up speech recognition callback
        voiceRecognitionManager.onSpeechComplete = { text ->
            _inputText.value = text
            processUserQuery(text)
        }

        // Track TTS speaking state
        viewModelScope.launch {
            ttsManager.isSpeaking.collect { isSpeaking ->
                if (isSpeaking) {
                    _assistantState.value = AssistantState.SPEAKING
                    _statusText.value = "MYRA is speaking..."
                } else if (_assistantState.value == AssistantState.SPEAKING) {
                    _assistantState.value = AssistantState.IDLE
                    _statusText.value = "Tap orb or mic to ask"
                }
            }
        }
    }

    fun startListening() {
        ttsManager.stop()
        _assistantState.value = AssistantState.LISTENING
        _statusText.value = "Listening to you..."
        voiceRecognitionManager.startListening()
    }

    fun stopListening() {
        voiceRecognitionManager.stopListening()
        if (_assistantState.value == AssistantState.LISTENING) {
            _assistantState.value = AssistantState.IDLE
            _statusText.value = "Tap orb or mic to ask"
        }
    }

    fun toggleListening() {
        if (voiceRecognitionManager.isListening.value) {
            stopListening()
        } else {
            startListening()
        }
    }

    fun onInputTextChanged(newText: String) {
        _inputText.value = newText
    }

    fun submitTextQuery() {
        val query = _inputText.value.trim()
        if (query.isNotEmpty()) {
            processUserQuery(query)
            _inputText.value = ""
        }
    }

    fun processUserQuery(query: String) {
        viewModelScope.launch {
            _assistantState.value = AssistantState.THINKING
            _statusText.value = "Thinking..."

            delay(350) // visual feedback

            val action = brainEngine.processQuery(query)
            _currentAction.value = action

            val responseToSpeak = when (action) {
                is ActionCommand.MakeCall -> {
                    _assistantState.value = AssistantState.ACTION_READY
                    _statusText.value = "Calling ${action.contactName}"
                    action.prompt
                }
                is ActionCommand.SendWhatsApp -> {
                    _assistantState.value = AssistantState.ACTION_READY
                    _statusText.value = "Drafted WhatsApp to ${action.recipientName}"
                    action.prompt
                }
                is ActionCommand.SendSms -> {
                    _assistantState.value = AssistantState.ACTION_READY
                    _statusText.value = "Drafted SMS to ${action.recipientName}"
                    action.prompt
                }
                is ActionCommand.OpenApp -> {
                    _assistantState.value = AssistantState.ACTION_READY
                    _statusText.value = "Launching ${action.appName}"
                    action.prompt
                }
                is ActionCommand.ReadScreen -> {
                    _assistantState.value = AssistantState.ACTION_READY
                    _statusText.value = "Screen analyzed: ${action.screenTitle}"
                    action.summary
                }
                is ActionCommand.GeneralAnswer -> {
                    _assistantState.value = AssistantState.IDLE
                    _statusText.value = "Answered with ${action.modelUsed}"
                    action.responseText
                }
            }

            // Speak out loud if auto-speak enabled
            if (repository.autoSpeak.value) {
                ttsManager.speak(
                    text = responseToSpeak,
                    rate = repository.speechRate.value,
                    pitch = repository.speechPitch.value
                )
            }

            // Log interaction
            val (type, details) = when (action) {
                is ActionCommand.MakeCall -> "CALL" to "${action.contactName} (${action.phoneNumber})"
                is ActionCommand.SendWhatsApp -> "WHATSAPP" to "${action.recipientName}: ${action.message}"
                is ActionCommand.SendSms -> "SMS" to "${action.recipientName}: ${action.message}"
                is ActionCommand.OpenApp -> "APP" to action.appName
                is ActionCommand.ReadScreen -> "SCREEN_READ" to action.screenTitle
                is ActionCommand.GeneralAnswer -> "AI_QUERY" to action.modelUsed
            }
            repository.logInteraction(
                query = query,
                response = responseToSpeak,
                actionType = type,
                actionDetails = details,
                modelUsed = repository.selectedProvider.value.displayName
            )
        }
    }

    fun executeCurrentAction() {
        val action = _currentAction.value ?: return
        when (action) {
            is ActionCommand.MakeCall -> {
                intentDispatcher.executeCall(action.phoneNumber)
            }
            is ActionCommand.SendWhatsApp -> {
                intentDispatcher.executeWhatsApp(action.phoneNumber, action.message)
            }
            is ActionCommand.SendSms -> {
                intentDispatcher.executeSms(action.phoneNumber, action.message)
            }
            is ActionCommand.OpenApp -> {
                intentDispatcher.openAppByName(action.appName, action.targetAction)
            }
            is ActionCommand.ReadScreen -> {
                speakText(action.summary)
            }
            is ActionCommand.GeneralAnswer -> {}
        }
        _currentAction.value = null
        _assistantState.value = AssistantState.IDLE
        _statusText.value = "Action completed"
    }

    fun dismissCurrentAction() {
        _currentAction.value = null
        _assistantState.value = AssistantState.IDLE
        _statusText.value = "Tap orb or mic to ask"
        ttsManager.stop()
    }

    fun speakText(text: String) {
        ttsManager.speak(
            text = text,
            rate = repository.speechRate.value,
            pitch = repository.speechPitch.value
        )
    }

    fun stopSpeaking() {
        ttsManager.stop()
    }

    fun updateOrbCustomization(customization: OrbCustomization) {
        repository.updateOrbCustomization(customization)
    }

    fun setAIProvider(provider: AIModelProvider) {
        repository.setSelectedProvider(provider)
    }

    fun setApiKey(provider: AIModelProvider, key: String) {
        repository.setApiKey(provider, key)
    }

    fun setCodeninjavikAccount(email: String) {
        repository.setCodeninjavikAccount(email)
    }

    fun addMemory(category: String, content: String, isImportant: Boolean = false) {
        viewModelScope.launch {
            repository.addMemory(category, content, isImportant)
        }
    }

    fun deleteMemory(memory: MemoryEntity) {
        viewModelScope.launch {
            repository.deleteMemory(memory)
        }
    }

    fun addContact(name: String, nickname: String, phoneNumber: String) {
        viewModelScope.launch {
            repository.addContact(name, nickname, phoneNumber)
        }
    }

    fun deleteContact(contact: ContactEntity) {
        viewModelScope.launch {
            repository.deleteContact(contact)
        }
    }

    fun analyzeScreenSample(sample: ScreenContentItem) {
        val action = ActionCommand.ReadScreen(
            screenTitle = sample.title,
            extractedText = sample.rawContent,
            summary = sample.summary,
            actionableElements = sample.suggestedActions
        )
        _currentAction.value = action
        _assistantState.value = AssistantState.ACTION_READY
        _statusText.value = "Read screen: ${sample.title}"
        if (repository.autoSpeak.value) {
            speakText(sample.summary)
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceRecognitionManager.stopListening()
        ttsManager.shutdown()
    }
}
