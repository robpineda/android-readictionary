package com.robertopineda.android_readictionary.views

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import com.robertopineda.android_readictionary.models.Language
import com.robertopineda.android_readictionary.models.TranslatedWord
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ReadingView(
    documentUri: Uri,
    translatedWords: SnapshotStateList<TranslatedWord>,
    targetLanguage: MutableState<Language>
) {
    val configuration = LocalConfiguration.current
    var dictionaryViewHeight by remember { mutableStateOf(configuration.screenHeightDp.dp / 2) }
    var isDragging by remember { mutableStateOf(false) }
    var highlightedWord by remember { mutableStateOf<String?>(null) }
    val cacheKeys = remember { mutableStateMapOf<Uri, String>() }

    // Decode the document URI
    val decodedUri = Uri.parse(Uri.decode(documentUri.toString()))

    // Access LocalDensity in a composable context
    val density = LocalDensity.current

    Box(modifier = Modifier.fillMaxSize()) {
        // PDFView (Top View)
        PDFView(
            documentUri = decodedUri,
            translatedWords = translatedWords,
            targetLanguage = targetLanguage,
            cacheKeys = cacheKeys,
            modifier = Modifier.fillMaxSize(),
        )

        // DictionaryView (Bottom Sheet)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dictionaryViewHeight)
                .align(Alignment.BottomCenter)
                .background(Color.White)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, dragAmount ->
                        // Convert sheetHeight to pixels for calculation
                        val currentHeightPx = with(density) { dictionaryViewHeight.toPx() }
                        val newHeightPx = currentHeightPx - dragAmount // Subtract dragAmount in pixels
                        val newHeightDp = with(density) { newHeightPx.toDp() }

                        // Update sheetHeight, ensuring it stays within bounds
                        dictionaryViewHeight = newHeightDp.coerceIn(100.dp, 400.dp)
                    }
                }
        ) {
            DictionaryView(
                translatedWords = translatedWords,
                highlightedWord = null,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}