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