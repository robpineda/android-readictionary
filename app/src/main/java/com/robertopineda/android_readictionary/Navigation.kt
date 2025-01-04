package com.robertopineda.android_readictionary

import android.net.Uri
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.robertopineda.android_readictionary.models.Language
import com.robertopineda.android_readictionary.models.TranslatedWord
import com.robertopineda.android_readictionary.views.DocumentListView
import com.robertopineda.android_readictionary.views.ReadingView

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val translatedWords = remember { mutableStateListOf<TranslatedWord>() }
    val targetLanguage = remember { mutableStateOf(Language.ENGLISH) }

    NavHost(
        navController = navController,
        startDestination = Screen.DocumentList.route
    ) {
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
    }
}