package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String, // e.g. "Personal", "Communication", "Habits", "Work", "Device"
    val content: String,
    val isImportant: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
