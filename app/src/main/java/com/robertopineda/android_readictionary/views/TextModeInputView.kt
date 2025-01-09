package com.robertopineda.android_readictionary.views

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.robertopineda.android_readictionary.Screen
import com.robertopineda.android_readictionary.models.Language
import com.robertopineda.android_readictionary.models.TextRecord
import com.robertopineda.android_readictionary.models.TranslatedWord
import com.robertopineda.android_readictionary.utilities.CacheManager
import com.robertopineda.android_readictionary.utilities.TranslationService
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextModeInputView(
    onSave: (String, String) -> TextRecord,
    navController: NavController,
    translatedWords: SnapshotStateList<TranslatedWord>,
    targetLanguage: MutableState<Language>,
    textRecords: SnapshotStateList<TextRecord>
) {
    val context = LocalContext.current
    val cacheManager = remember { CacheManager.getInstance(context) }

    var name by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Title TextField
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Title") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        // Text TextField
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Write your text to translate...") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 16.dp),
            singleLine = false
        )

        // Translate Button at the bottom
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Button(
                onClick = {
                    if (name.isEmpty()) {
                        name = text.take(15)
                    }
                    val newRecord = onSave(name, text)

                    // Save the new record (name and text) to the cache
                    val cacheKey = cacheManager.getCacheKeyForTextRecord(newRecord.name,
                        newRecord.text)
                    cacheManager.saveTextRecord(newRecord, cacheKey)

                    // Add the new record to the textRecords list
                    textRecords.add(newRecord)

                    // Navigate to TextModeDetailView
                    navController.navigate(Screen.TextModeDetailView.createRoute(newRecord.id.toString())) {
                        popUpTo(Screen.TextModeInputView.route) { inclusive = true }
                    }
                },
                enabled = text.isNotEmpty()
            ) {
                Text("Translate")
            }
        }
    }
}