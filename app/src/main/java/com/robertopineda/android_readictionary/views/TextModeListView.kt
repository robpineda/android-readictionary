package com.robertopineda.android_readictionary.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.robertopineda.android_readictionary.models.Language
import com.robertopineda.android_readictionary.models.TextRecord
import com.robertopineda.android_readictionary.models.TranslatedWord

@Composable
fun TextModeListView(
    navController: NavController,
    translatedWords: SnapshotStateList<TranslatedWord>,
    targetLanguage: MutableState<Language>
) {
    val textRecords = remember { mutableStateListOf<TextRecord>() }
    var showTextInputView by remember { mutableStateOf(false) }
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Text Translations") }, actions = {
                IconButton(onClick = { showTextInputView = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Text")
                }
            })
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(textRecords) { record ->
                NavigationLink(
                    destination = { TextModeDetailView(record, translatedWords, targetLanguage) }
                ) {
                    Text(record.name)
                }
            }
        }
    }

    if (showTextInputView) {
        TextModeInputView { name, text ->
            textRecords.add(TextRecord(name = name, text = text, translatedWords = emptyList()))
            showTextInputView = false
        }
    }
}