package com.robertopineda.android_readictionary

import android.net.Uri
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.robertopineda.android_readictionary.models.Language
import com.robertopineda.android_readictionary.models.TextRecord
import com.robertopineda.android_readictionary.models.TranslatedWord
import com.robertopineda.android_readictionary.views.ContentView
import com.robertopineda.android_readictionary.views.DocumentListView
import com.robertopineda.android_readictionary.views.ReadingView
import com.robertopineda.android_readictionary.views.TextModeDetailView

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val translatedWords = remember { mutableStateListOf<TranslatedWord>() }
    val targetLanguage = remember { mutableStateOf(Language.ENGLISH) }
    val textRecords = remember { mutableStateListOf<TextRecord>() }

    NavHost(
        navController = navController,
        startDestination = Screen.ContentView.route
    ) {
        // ContentView Screen
        composable(Screen.ContentView.route) {
            ContentView(
                navController = navController,
                translatedWords = translatedWords,
                targetLanguage = targetLanguage,
                textRecords = textRecords
            )
        }

        // DocumentList Screen
        composable(Screen.DocumentList.route) {
            DocumentListView(
                navController = navController,
                translatedWords = translatedWords,
                targetLanguage = targetLanguage
            )
        }

        // ReadingView Screen
        composable(Screen.ReadingView.route) { backStackEntry ->
            val documentUri = backStackEntry.arguments?.getString("documentUri") ?: ""
            ReadingView(
                documentUri = Uri.parse(documentUri),
                translatedWords = translatedWords,
                targetLanguage = targetLanguage
            )
        }

        // TextModeDetailView Screen
        composable("textModeDetailView/{recordId}") { backStackEntry ->
            val recordId = backStackEntry.arguments?.getString("recordId") ?: ""
            val record = textRecords.find { it.id.toString() == recordId } ?: return@composable
            TextModeDetailView(
                record = record,
                translatedWords = translatedWords,
                targetLanguage = targetLanguage
            )
        }
    }
}