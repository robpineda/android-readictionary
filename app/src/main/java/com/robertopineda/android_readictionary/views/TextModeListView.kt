package com.robertopineda.android_readictionary.views

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.robertopineda.android_readictionary.Screen
import com.robertopineda.android_readictionary.models.Language
import com.robertopineda.android_readictionary.models.TextRecord
import com.robertopineda.android_readictionary.models.TranslatedWord
import com.robertopineda.android_readictionary.utilities.CacheManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextModeListView(
    navController: NavController,
    translatedWords: SnapshotStateList<TranslatedWord>,
    targetLanguage: MutableState<Language>,
    textRecords: SnapshotStateList<TextRecord>,
) {
    val context = LocalContext.current
    val cacheManager = remember { CacheManager.getInstance(context) }

    // State for the dialog
    var showDialog by remember { mutableStateOf(false) }
    var selectedRecord by remember { mutableStateOf<TextRecord?>(null) }

    // Ensure the content fills the available space
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Text Translations") },
                    actions = {
                    IconButton(onClick = {
                        // Navigate to TextModeInputView
                        navController.navigate(Screen.TextModeInputView.route)
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Text")
                    }
                })
            }
        ) { padding ->
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(textRecords) { record ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = {
                                        Log.d("TextModeListView", "Tapped on record: $record")
                                        navController.navigate("textModeDetailView/${record.id}")
                                    },
                                    onLongPress = {
                                        selectedRecord = record
                                        showDialog = true
                                    },
                                )
                            }
                            .padding(16.dp)
                    ) {
                        Text(
                            text = record.name,
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    }

    // Dialog for Delete and other options
    if (showDialog && selectedRecord != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete record?") },
            confirmButton = {
                Button(
                    onClick = {
                        // Delete the selected record
                        val recordToDelete = selectedRecord!!
                        textRecords.remove(recordToDelete)

                        // Delete the cache associated with the record
                        val cacheKey = cacheManager.cacheKey(recordToDelete.text)
                        cacheManager.deleteCachedWords(cacheKey)

                        showDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}