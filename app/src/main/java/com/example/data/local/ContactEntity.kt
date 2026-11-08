package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val nickname: String, // e.g. "Mom", "Alex", "Boss", "Dad", "Sarah"
    val phoneNumber: String,
    val isFavorite: Boolean = true
)
