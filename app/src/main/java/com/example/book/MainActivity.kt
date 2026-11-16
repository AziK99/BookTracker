package com.example.book // <--- Правильный пакет (без readingtracker)

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// Обратите внимание на импорты: com.example.book, а не readingtracker
import com.example.book.data.AppDatabase
import com.example.book.data.ui.HistoryScreen
import com.example.book.data.ui.ReadingScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dao = AppDatabase.getDatabase(applicationContext).readingSessionDao()

        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "reading") {
                    composable(route = "reading") {
                        ReadingScreen(
                            onNavigateToHistory = { navController.navigate(route = "history") },
                            dao = dao // <--- ВАЖНО: Передаем dao сюда!
                        )
                    }
                    composable(route = "history") {
                        HistoryScreen(dao = dao)
                    }
                }
            }
        }
    }
}
fun formatMillis(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / 60000) % 60
    return "%02d:%02d".format(minutes, seconds)
}