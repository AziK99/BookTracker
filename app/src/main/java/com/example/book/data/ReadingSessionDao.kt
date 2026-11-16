package com.example.book.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingSessionDao {
    @Insert
    suspend fun insert(session: ReadingSession)

    @Query("SELECT * FROM reading_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<ReadingSession>>

    @Query("SELECT AVG(wordsRead * 60000.0 / durationMillis) FROM reading_sessions WHERE durationMillis > 0")
    fun getAverageWpm(): Flow<Double?>
}