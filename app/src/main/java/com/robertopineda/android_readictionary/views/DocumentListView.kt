package com.robertopineda.android_readictionary.views

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.robertopineda.android_readictionary.models.Language
import com.robertopineda.android_readictionary.models.TranslatedWord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentListView(
    navController: NavController, // Pass NavController as a parameter
    translatedWords: SnapshotStateList<TranslatedWord>,
    targetLanguage: MutableState<Language>
) {
    val documents = remember { mutableStateListOf<Uri>() }
    var showDocumentPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Library") },
                actions = {
                    IconButton(onClick = { showDocumentPicker = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Document")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(documents) { document ->
                // Use a Button or Clickable Text for navigation
                Text(
                    text = document.lastPathSegment ?: "Unknown",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // Navigate to the ReadingView
                            navController.navigate("readingView/${document.toString()}")
                        }
                        .padding(16.dp)
                )
            }
        }
    }

    if (showDocumentPicker) {
        DocumentPicker { uri ->
            documents.add(uri)
            showDocumentPicker = false
        }
    }
}