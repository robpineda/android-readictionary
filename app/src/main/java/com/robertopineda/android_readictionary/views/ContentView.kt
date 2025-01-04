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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.robertopineda.android_readictionary.models.Language
import com.robertopineda.android_readictionary.models.TranslatedWord

@Composable
fun ContentView() {
    val navController = rememberNavController()
    val translatedWords = remember { mutableStateListOf<TranslatedWord>() }
    val targetLanguage = remember { mutableStateOf(Language.ENGLISH) }

    TabLayout(navController, translatedWords, targetLanguage)
}

@Composable
fun TabLayout(
    navController: NavController,
    translatedWords: SnapshotStateList<TranslatedWord>,
    targetLanguage: MutableState<Language>
) {
    var selectedTab by remember { mutableStateOf(0) }

    Column {
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

        when (selectedTab) {
            0 -> DocumentListView(navController, translatedWords, targetLanguage)
            1 -> TextModeListView(navController, translatedWords, targetLanguage)
        }
    }
}