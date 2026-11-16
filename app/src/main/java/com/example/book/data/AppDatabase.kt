package com.example.book.data // <--- ГЛАВНОЕ ИСПРАВЛЕНИЕ: убираем readingtracker

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ReadingSession::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun readingSessionDao(): ReadingSessionDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "reading_tracker_db"
                ).build().also { INSTANCE = it }
            }
    }
}