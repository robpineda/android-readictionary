package com.robertopineda.android_readictionary.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.robertopineda.android_readictionary.models.TranslatedWord

@Composable
fun TranslatedWordRow(
    word: TranslatedWord,
    isHighlighted: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isHighlighted) Color.Yellow else Color.Transparent)
            .padding(vertical = 4.dp)
    ) {
        if (word.transliteration?.isNotEmpty() == true) {
            Text(
                text = "${word.originalText}, ${word.transliteration}, ${word.romaji ?: ""}",
                style = MaterialTheme.typography.headlineSmall
            )
        } else {
            Text(
                text = "${word.originalText}, ${word.romaji ?: ""}",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        if (word.definitions.isNotEmpty()) {
            Text(
                text = word.definitions.joinToString(", "),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}