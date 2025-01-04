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
    height: Float,
    isDragging: Boolean,
    translatedWords: List<TranslatedWord>,
    highlightedWord: String?
) {
    Column(modifier = Modifier.height(height.dp)) {
        // Drag Handle
        Box(
            modifier = Modifier
                .height(8.dp)
                .fillMaxWidth()
                .background(Color.Gray)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        // Update height based on drag
                    }
                }
        )

        // Translated Words List
        LazyColumn {
            items(translatedWords) { word ->
                TranslatedWordRow(
                    word = word,
                    isHighlighted = word.originalText == highlightedWord
                )
            }
        }
    }
}