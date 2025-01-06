package com.robertopineda.android_readictionary.views

import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.TabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.robertopineda.android_readictionary.models.Language
import com.robertopineda.android_readictionary.models.TextRecord
import com.robertopineda.android_readictionary.models.TranslatedWord
import com.robertopineda.android_readictionary.utilities.SharedPreferencesHelper

@Composable
fun ContentView(
    navController: NavController,
    translatedWords: SnapshotStateList<TranslatedWord>,
    targetLanguage: MutableState<Language>,
    textRecords: SnapshotStateList<TextRecord>,
    documents: SnapshotStateList<Uri>
) {

    val context = LocalContext.current
    val sharedPreferencesHelper = remember { SharedPreferencesHelper(context) }

    // Load documents from SharedPreferences when the app starts
    LaunchedEffect(Unit) {
        try {
            val savedDocuments = sharedPreferencesHelper.loadDocuments()
            documents.clear()
            documents.addAll(savedDocuments)
        } catch (e: Exception) {
            // Handle the error (e.g., log it or show a message to the user)
            println("Error loading documents: ${e.message}")
        }
    }

    // Save documents to SharedPreferences whenever the list changes
//    LaunchedEffect(documents) {
//        sharedPreferencesHelper.saveDocuments(documents)
//    }

    TabLayout(
        navController = navController,
        translatedWords = translatedWords,
        targetLanguage = targetLanguage,
        textRecords = textRecords,
        documents = documents
    )
}

@Composable
fun TabLayout(
    navController: NavController,
    translatedWords: SnapshotStateList<TranslatedWord>,
    targetLanguage: MutableState<Language>,
    textRecords: SnapshotStateList<TextRecord>,
    documents: SnapshotStateList<Uri>
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("PDF") },
                icon = { Icon(Icons.Default.Info, contentDescription = "PDF") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Text") },
                icon = { Icon(Icons.Default.Create, contentDescription = "Text") }
            )
        }

        // Content for the selected tab
        when (selectedTab) {
            0 -> DocumentListView(navController, translatedWords, targetLanguage, documents)
            1 -> TextModeListView(navController, translatedWords, targetLanguage, textRecords)
        }
    }
}