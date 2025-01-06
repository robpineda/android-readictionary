package com.robertopineda.android_readictionary.views

import android.net.Uri
import android.util.Log
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.robertopineda.android_readictionary.utilities.*
import com.robertopineda.android_readictionary.Screen
import com.robertopineda.android_readictionary.models.Language
import com.robertopineda.android_readictionary.models.TranslatedWord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentListView(
    navController: NavController, // Pass NavController as a parameter
    translatedWords: SnapshotStateList<TranslatedWord>,
    targetLanguage: MutableState<Language>,
    documents: SnapshotStateList<Uri>
) {
    var showDocumentPicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sharedPreferencesHelper = remember { SharedPreferencesHelper(context) }

    // Ensure the content fills the available space
    Box(modifier = Modifier.fillMaxSize()) {
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
                    val fileName = try {
                        getFileNameFromUri(context, document) ?: "Unknown"
                    } catch (e: SecurityException) {
                        Log.e("DocumentListView", "Permission denied for URI: $document", e)
                        "Permission Denied"
                    }
                    Text(
                        text = fileName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                try {
                                    navController.navigate(Screen.ReadingView.createRoute(document.toString()))
                                } catch (e: SecurityException) {
                                    Log.e("DocumentListView", "Permission denied for URI: $document", e)
                                }
                            }
                            .padding(16.dp)
                    )
                }
            }
        }
    }

    if (showDocumentPicker) {
        DocumentPicker { uri ->
            documents.add(uri)
            sharedPreferencesHelper.saveDocuments(documents)
            showDocumentPicker = false
        }
    }
}