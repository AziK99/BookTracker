package com.example.book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.book.data.ReadingSession
import com.example.book.data.ReadingSessionDao
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class ReadingUiState(
    val isRunning: Boolean = false,
    val elapsedMillis: Long = 0L,
    val wordCount: Int = 0,
    val currentWpm: Double = 0.0,
    val bookTitle: String = ""
)

class ReadingViewModel(private val dao: ReadingSessionDao) : ViewModel() {

    private val _uiState = MutableStateFlow(ReadingUiState())
    val uiState: StateFlow<ReadingUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var startTime: Long = 0L
    private var accumulatedTime: Long = 0L

    fun updateBookTitle(title: String) {
        _uiState.update { it.copy(bookTitle = title) }
    }

    fun startSession() {
        if (_uiState.value.isRunning) return
        startTime = System.currentTimeMillis()
        _uiState.update { it.copy(isRunning = true) }

        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(100)
                val elapsed = accumulatedTime + (System.currentTimeMillis() - startTime)
                _uiState.update { state ->
                    val wpm = if (elapsed > 0) state.wordCount.toDouble() / (elapsed / 60000.0) else 0.0
                    state.copy(elapsedMillis = elapsed, currentWpm = wpm)
                }
            }
        }
    }

    fun pauseSession() {
        if (!_uiState.value.isRunning) return
        timerJob?.cancel()
        accumulatedTime = _uiState.value.elapsedMillis
        _uiState.update { it.copy(isRunning = false) }
    }

    fun finishSession() {
        timerJob?.cancel()
        val state = _uiState.value
        if (state.elapsedMillis > 0 && state.wordCount > 0) {
            viewModelScope.launch {
                dao.insert(
                    ReadingSession(
                        bookTitle = state.bookTitle.ifBlank { "Без названия" },
                        wordsRead = state.wordCount,
                        durationMillis = state.elapsedMillis
                    )
                )
                resetState()
            }
        } else {
            resetState()
        }
    }

    fun updateWordCount(count: Int) {
        _uiState.update { state ->

            val wpm = if (state.elapsedMillis > 0) {
                count.toDouble() / (state.elapsedMillis / 60000.0)
            } else 0.0

            state.copy(wordCount = count, currentWpm = wpm)
        }
    }

    private fun resetState() {
        accumulatedTime = 0L
        _uiState.update { ReadingUiState() }
    }
}