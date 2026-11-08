package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [MemoryEntity::class, ContactEntity::class, InteractionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MyraDatabase : RoomDatabase() {
    abstract fun myraDao(): MyraDao

    companion object {
        @Volatile
        private var INSTANCE: MyraDatabase? = null

        fun getDatabase(context: Context): MyraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyraDatabase::class.java,
                    "myra_assistant_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                populateInitialData(getDatabase(context).myraDao())
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateInitialData(dao: MyraDao) {
            // Seed sample contacts for instant voice calling & WhatsApp
            dao.insertContacts(
                listOf(
                    ContactEntity(name = "Mom", nickname = "Mom", phoneNumber = "+1-555-0199", isFavorite = true),
                    ContactEntity(name = "Alex Vance", nickname = "Alex", phoneNumber = "+1-555-0142", isFavorite = true),
                    ContactEntity(name = "Dad", nickname = "Dad", phoneNumber = "+1-555-0188", isFavorite = true),
                    ContactEntity(name = "Dr. Sarah Rao", nickname = "Sarah", phoneNumber = "+1-555-0123", isFavorite = false)
                )
            )

            // Seed remembered preferences from codeninjavik.in
            dao.insertMemories(
                listOf(
                    MemoryEntity(
                        category = "Communication",
                        content = "User prefers WhatsApp over SMS for close contacts like Mom & Alex",
                        isImportant = true
                    ),
                    MemoryEntity(
                        category = "Personal",
                        content = "Name: Vikram | Account: vikram@codeninjavik.in (Pro Developer Tier)",
                        isImportant = true
                    ),
                    MemoryEntity(
                        category = "Tone & Style",
                        content = "Keep voice replies concise, witty, and directly actionable under 2 sentences",
                        isImportant = true
                    ),
                    MemoryEntity(
                        category = "Device Routine",
                        content = "Default music streamer is Spotify; prefers dark neon orb theme",
                        isImportant = false
                    ),
                    MemoryEntity(
                        category = "Work & Schedule",
                        content = "Morning standup starts at 9:30 AM; focus mode during 2 PM - 5 PM",
                        isImportant = false
                    )
                )
            )
        }
    }
}
