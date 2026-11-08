package com.example.data

import com.example.data.local.ContactEntity
import com.example.data.local.InteractionEntity
import com.example.data.local.MemoryEntity
import com.example.data.local.MyraDao
import com.example.model.AIModelProvider
import com.example.model.OrbCustomization
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class MyraRepository(
    private val dao: MyraDao,
    private val preferencesManager: UserPreferencesManager
) {
    val orbCustomization: StateFlow<OrbCustomization> = preferencesManager.orbCustomization
    val selectedProvider: StateFlow<AIModelProvider> = preferencesManager.selectedProvider
    val codeninjavikAccount: StateFlow<String> = preferencesManager.codeninjavikAccount
    val speechRate: StateFlow<Float> = preferencesManager.speechRate
    val speechPitch: StateFlow<Float> = preferencesManager.speechPitch
    val autoSpeak: StateFlow<Boolean> = preferencesManager.autoSpeak

    // Memories
    fun getAllMemories(): Flow<List<MemoryEntity>> = dao.getAllMemories()

    suspend fun addMemory(category: String, content: String, isImportant: Boolean = false) {
        dao.insertMemory(MemoryEntity(category = category, content = content, isImportant = isImportant))
    }

    suspend fun updateMemory(memory: MemoryEntity) {
        dao.updateMemory(memory)
    }

    suspend fun deleteMemory(memory: MemoryEntity) {
        dao.deleteMemory(memory)
    }

    // Contacts
    fun getAllContacts(): Flow<List<ContactEntity>> = dao.getAllContacts()

    suspend fun findContact(query: String): ContactEntity? {
        return dao.findContactByNickname(query)
    }

    suspend fun addContact(name: String, nickname: String, phoneNumber: String) {
        dao.insertContact(ContactEntity(name = name, nickname = nickname, phoneNumber = phoneNumber))
    }

    suspend fun deleteContact(contact: ContactEntity) {
        dao.deleteContact(contact)
    }

    // Interactions
    fun getRecentInteractions(): Flow<List<InteractionEntity>> = dao.getRecentInteractions()

    suspend fun logInteraction(
        query: String,
        response: String,
        actionType: String,
        actionDetails: String? = null,
        modelUsed: String = "Gemini"
    ) {
        dao.insertInteraction(
            InteractionEntity(
                query = query,
                response = response,
                actionType = actionType,
                actionDetails = actionDetails,
                modelUsed = modelUsed
            )
        )
    }

    suspend fun clearHistory() {
        dao.clearHistory()
    }

    // Preferences
    fun updateOrbCustomization(customization: OrbCustomization) {
        preferencesManager.updateOrbCustomization(customization)
    }

    fun setSelectedProvider(provider: AIModelProvider) {
        preferencesManager.setSelectedProvider(provider)
    }

    fun getApiKey(provider: AIModelProvider): String {
        return preferencesManager.getApiKey(provider)
    }

    fun setApiKey(provider: AIModelProvider, key: String) {
        preferencesManager.setApiKey(provider, key)
    }

    fun setCodeninjavikAccount(email: String) {
        preferencesManager.setCodeninjavikAccount(email)
    }

    fun setSpeechSettings(rate: Float, pitch: Float, autoSpeak: Boolean) {
        preferencesManager.setSpeechSettings(rate, pitch, autoSpeak)
    }
}
