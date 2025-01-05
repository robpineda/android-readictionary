package com.robertopineda.android_readictionary.views

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalConfiguration
import com.robertopineda.android_readictionary.models.Language
import com.robertopineda.android_readictionary.models.TranslatedWord
import androidx.compose.ui.Modifier

@Composable
fun ReadingView(
    documentUri: Uri,
    translatedWords: SnapshotStateList<TranslatedWord>,
    targetLanguage: MutableState<Language>
) {
    // Use `remember` to initialize `dictionaryViewHeight` in a composable context
    val configuration = LocalConfiguration.current
    var dictionaryViewHeight by remember { mutableStateOf(configuration.screenHeightDp / 2) }
    var isDragging by remember { mutableStateOf(false) }
    var highlightedWord by remember { mutableStateOf<String?>(null) }

    Column {
        // Language Picker
        Row {
            Text("Target Language")
            Spacer(modifier = Modifier.weight(1f)) // Add modifier
            DropdownMenu(
                expanded = false,
                onDismissRequest = { /* Handle dismiss */ }
            ) {
                Language.entries.forEach { language ->
                    DropdownMenuItem(
                        text = { Text(language.name) }, // Add `text` parameter
                        onClick = { targetLanguage.value = language }
                    )
                }
            }
        }

        // PDF Reader
        if (documentUri.toString().endsWith(".pdf")) {
            PDFView(documentUri, translatedWords, targetLanguage)
        } else {
            Text("Unsupported file format")
        }

        // Dictionary View
        DictionaryView(
            height = dictionaryViewHeight.toFloat(),
            isDragging = isDragging,
            translatedWords = translatedWords,
            highlightedWord = highlightedWord
        )
    }
}