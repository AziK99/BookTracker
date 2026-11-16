package com.example.book.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reading_sessions")
data class ReadingSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookTitle: String,
    val wordsRead: Int,
    val durationMillis: Long,
    val timestamp: Long = System.currentTimeMillis()
) {
    val wpm: Double
        get() = if (durationMillis > 0) {
            wordsRead.toDouble() / (durationMillis / 60000.0)
        } else 0.0
}