package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "interactions")
data class InteractionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val query: String,
    val response: String,
    val actionType: String, // CALL, WHATSAPP, SMS, APP_LAUNCH, SCREEN_READ, CHAT
    val actionDetails: String? = null,
    val modelUsed: String = "Gemini",
    val timestamp: Long = System.currentTimeMillis()
)
