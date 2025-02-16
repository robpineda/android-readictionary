package com.robertopineda.android_readictionary.views

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.robertopineda.android_readictionary.models.TranslatedWord

@Composable
fun TranslatedWordRow(
    word: TranslatedWord,
    isHighlighted: Boolean,
    onWordTapped: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isHighlighted) Color(0x33FFA500) else Color.Transparent) // Light orange background for highlighted rows
            .pointerInput(Unit) {
                detectTapGestures {
                    onWordTapped()
                }
            }
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            if (word.transliteration?.isNotEmpty() == true) {
                Text(
                    text = "${word.originalText}, ${word.transliteration}, ${word.romaji ?: ""}",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = if (isHighlighted) Color(0xFFFFA500) else Color.Unspecified
                    )
                )
            } else {
                Text(
                    text = "${word.originalText}, ${word.romaji ?: ""}",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = if (isHighlighted) Color(0xFFFFA500) else Color.Unspecified
                    )
                )
            }

            //Definitions
            if (word.definitions.isNotEmpty()) {
                Text(
                    text = word.definitions.joinToString(", "),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isHighlighted) Color(0xFFFFA500) else Color.Unspecified
                    ),
                    color = Color.Gray
                )
            }
        }
    }
}