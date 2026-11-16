package com.example.book.data.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.book.ReadingViewModel
import com.example.book.data.ReadingSessionDao

@Composable
fun ReadingScreen(
    onNavigateToHistory: () -> Unit,
    dao: ReadingSessionDao
) {
    val viewModel: ReadingViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ReadingViewModel(dao) as T
            }
        }
    )

    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        OutlinedTextField(
            value = state.bookTitle,
            onValueChange = { viewModel.updateBookTitle(it) },
            label = { Text("Название книги") },
            enabled = !state.isRunning,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Время: ${formatMillis(state.elapsedMillis)}",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Слов: ${state.wordCount}",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "WPM: %.0f".format(state.currentWpm),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(32.dp))


        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { viewModel.startSession() }, enabled = !state.isRunning) {
                Text("Старт")
            }
            Button(onClick = { viewModel.pauseSession() }, enabled = state.isRunning) {
                Text("Пауза")
            }
            Button(onClick = { viewModel.finishSession() }) {
                Text("Завершить")
            }
        }

        Spacer(Modifier.height(16.dp))


        var wordInput by remember { mutableStateOf("") }
        OutlinedTextField(
            value = wordInput,
            onValueChange = { newText ->

                if (newText.isEmpty() || newText.all { it.isDigit() }) {
                    wordInput = newText

                    val count = newText.toIntOrNull() ?: 0
                    viewModel.updateWordCount(count)
                }
            },
            label = { Text("Количество слов") }
        )

        Spacer(Modifier.height(24.dp))

        Button(onClick = onNavigateToHistory) {
            Text("Посмотреть историю")
        }
    }
}

fun formatMillis(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / 60000) % 60
    return "%02d:%02d".format(minutes, seconds)
}