package com.robertopineda.android_readictionary.views

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.robertopineda.android_readictionary.models.TranslatedWord

@Composable
fun DictionaryView(
    translatedWords: List<TranslatedWord>,
    highlightedWord: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .background(Color(0xFF1E1E1E))
            .padding(16.dp)
    ) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(translatedWords, key = { it.id }) { word ->
                TranslatedWordRow(
                    word = word,
                    isHighlighted = word.originalText == highlightedWord
                )
            }
        }
    }
}