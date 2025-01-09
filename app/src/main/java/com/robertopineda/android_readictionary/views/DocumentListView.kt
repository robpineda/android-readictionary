package com.robertopineda.android_readictionary.views

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
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
    val context = LocalContext.current
    val cacheManager = remember { CacheManager.getInstance(context) }
    val cacheKeys = remember { mutableStateMapOf<Uri, String>() }

    var showDocumentPicker by remember { mutableStateOf(false) }

    val sharedPreferencesHelper = remember { SharedPreferencesHelper(context) }

    // State for the dialog
    var showDialog by remember { mutableStateOf(false) }
    var selectedDocument by remember { mutableStateOf<Uri?>(null) }

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

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = {
                                        Log.d("DocumentListView", "Tapped on document: $document")
                                        try {
                                            val encodedUri = Uri.encode(document.toString())
                                            navController.navigate(Screen.ReadingView.createRoute(encodedUri))
                                            Log.d("DocumentListView", "Navigation triggered")
                                        } catch (e: SecurityException) {
                                            Log.e("DocumentListView", "Permission denied for URI: $document", e)
                                        } catch (e: Exception) {
                                            Log.e("DocumentListView", "Navigation failed: ${e.message}", e)
                                        }
                                    },
                                    onLongPress = {
                                        selectedDocument = document
                                        showDialog = true
                                    },
                                )
                            }
                            .padding(16.dp)
                    ) {
                        Text(text = fileName)
                    }
                }
            }
        }
    }

    // Dialog for Delete and other options
    if (showDialog && selectedDocument != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false }, // Close the dialog when dismissed
            title = { Text("Delete file?") },
            confirmButton = {
                Button(
                    onClick = {
                        // Delete the selected document
                        val documentToDelete = selectedDocument!!
                        documents.remove(documentToDelete)

                        // Delete the cache associated with the document
                        val cacheKey = cacheKeys[documentToDelete]
                        if (cacheKey != null) {
                            cacheManager.deleteCachedWords(cacheKey)
                            cacheKeys.remove(documentToDelete)
                        }

                        sharedPreferencesHelper.saveDocuments(documents)
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

    if (showDocumentPicker) {
        DocumentPicker { uri ->
            documents.add(uri)
            sharedPreferencesHelper.saveDocuments(documents)
            showDocumentPicker = false
        }
    }
}