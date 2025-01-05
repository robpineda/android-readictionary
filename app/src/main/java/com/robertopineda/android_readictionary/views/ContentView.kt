package com.robertopineda.android_readictionary.views

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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.robertopineda.android_readictionary.models.Language
import com.robertopineda.android_readictionary.models.TextRecord
import com.robertopineda.android_readictionary.models.TranslatedWord

@Composable
fun ContentView(
    navController: NavController,
    translatedWords: SnapshotStateList<TranslatedWord>,
    targetLanguage: MutableState<Language>,
    textRecords: SnapshotStateList<TextRecord>
) {
    TabLayout(
        navController = navController,
        translatedWords = translatedWords,
        targetLanguage = targetLanguage,
        textRecords = textRecords
    )
}

@Composable
fun TabLayout(
    navController: NavController,
    translatedWords: SnapshotStateList<TranslatedWord>,
    targetLanguage: MutableState<Language>,
    textRecords: SnapshotStateList<TextRecord>
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
            0 -> DocumentListView(navController, translatedWords, targetLanguage)
            1 -> TextModeListView(navController, translatedWords, targetLanguage, textRecords)
        }
    }
}