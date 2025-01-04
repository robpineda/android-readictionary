package com.robertopineda.android_readictionary.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.robertopineda.android_readictionary.models.Language
import com.robertopineda.android_readictionary.models.TextRecord
import com.robertopineda.android_readictionary.models.TranslatedWord

@Composable
fun TextModeDetailView(
    record: TextRecord,
    translatedWords: SnapshotStateList<TranslatedWord>,
    targetLanguage: MutableState<Language>
) {
    Column {
        // Display the original text
        Text(record.text)

        // Display translated words
        LazyColumn {
            items(translatedWords) { word ->
                TranslatedWordRow(word = word, isHighlighted = false)
            }
        }
    }
}