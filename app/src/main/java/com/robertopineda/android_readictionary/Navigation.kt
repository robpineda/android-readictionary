package com.robertopineda.android_readictionary

import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.robertopineda.android_readictionary.models.Language
import com.robertopineda.android_readictionary.models.TextRecord
import com.robertopineda.android_readictionary.models.TranslatedWord
import com.robertopineda.android_readictionary.views.ContentView
import com.robertopineda.android_readictionary.views.DocumentListView
import com.robertopineda.android_readictionary.views.ReadingView
import com.robertopineda.android_readictionary.views.TextModeDetailView
import com.robertopineda.android_readictionary.views.TextModeInputView
import com.robertopineda.android_readictionary.views.TextModeListView

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val translatedWords = remember { mutableStateListOf<TranslatedWord>() }
    val targetLanguage = remember { mutableStateOf(Language.ENGLISH) }
    val textRecords = remember { mutableStateListOf<TextRecord>() }
    val documents = remember { mutableStateListOf<Uri>() }

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
                textRecords = textRecords,
                documents = documents
            )
        }

        // DocumentListView Screen
        composable(
            route = Screen.DocumentListView.route,
        ) { backStackEntry ->
            DocumentListView(
                navController = navController,
                translatedWords = translatedWords,
                targetLanguage = targetLanguage,
                documents = documents
            )
        }

        // TextModeListView Screen
        composable(
            route = Screen.TextModeListView.route,
        ) { backStackEntry ->
            TextModeListView(
                navController = navController,
                translatedWords = translatedWords,
                targetLanguage = targetLanguage,
                textRecords = textRecords
            )
        }

        // ReadingView Screen
        composable(
            route = Screen.ReadingView.route,
            arguments = listOf(navArgument("documentUri") { type = NavType.StringType })
        ) { backStackEntry ->
            val documentUri = backStackEntry.arguments?.getString("documentUri") ?: ""
            ReadingView(
                documentUri = Uri.parse(documentUri),
                translatedWords = translatedWords,
                targetLanguage = targetLanguage
            )
        }

        // TextModeInputView Screen
        composable(Screen.TextModeInputView.route) {
            TextModeInputView(
                onSave = { name, text ->
                    val newRecord = TextRecord(name = name, text = text, translatedWords = emptyList())
                    textRecords.add(newRecord)
                    newRecord
                },
                navController = navController,
                translatedWords = translatedWords,
                targetLanguage = targetLanguage,
                textRecords = textRecords
            )
        }

        // TextModeDetailView Screen (reusable for both TextModeListView and TextModeInputView)
        composable(
            route = "textModeDetailView/{recordId}",
            arguments = listOf(navArgument("recordId") { type = NavType.StringType })
        ) { backStackEntry ->
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