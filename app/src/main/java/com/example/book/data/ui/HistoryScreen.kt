package com.example.book.data.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.book.data.ReadingSessionDao

@Composable
fun HistoryScreen(dao: ReadingSessionDao) {
    val sessions by dao.getAllSessions().collectAsState(initial = emptyList())
    val avgWpm by dao.getAverageWpm().collectAsState(initial = null)

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            Text(
                text = "Средний WPM: ${avgWpm?.let { "%.0f".format(it) } ?: "—"}",
                style = MaterialTheme.typography.titleLarge
            )
            HorizontalDivider(Modifier.padding(vertical = 12.dp))
        }
        items(sessions) { session ->
            ListItem(
                headlineContent = { Text(session.bookTitle) },
                supportingContent = {
                    Text("${session.wordsRead} слов · ${formatMillis(session.durationMillis)}")
                },
                trailingContent = {
                    Text("%.0f WPM".format(session.wpm), style = MaterialTheme.typography.titleMedium)
                }
            )
            HorizontalDivider()
        }
    }
}
