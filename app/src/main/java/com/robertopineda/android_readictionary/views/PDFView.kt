package com.robertopineda.android_readictionary.views

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavDeepLinkRequest.Builder.Companion.fromUri
import com.github.barteksc.pdfviewer.PDFView
import com.robertopineda.android_readictionary.models.Language
import com.robertopineda.android_readictionary.models.TranslatedWord
import com.robertopineda.android_readictionary.utilities.CacheManager
import com.robertopineda.android_readictionary.utilities.TranslationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import java.io.InputStream

@Composable
fun PDFView(
    documentUri: Uri,
    translatedWords: SnapshotStateList<TranslatedWord>,
    targetLanguage: MutableState<Language>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var extractedText by remember { mutableStateOf("") }

    // Load PDF and extract text
    LaunchedEffect(documentUri) {
        Log.d("PDFView", "Document URI: $documentUri")

        withContext(Dispatchers.IO) {
            val inputStream = context.contentResolver.openInputStream(documentUri)
            if (inputStream != null) {
                extractedText = extractTextFromPdf(inputStream)
            } else {
                Log.e("PDFView", "Failed to open input stream for URI: $documentUri")
            }
        }
    }

    // Check cache and translate text if necessary
    LaunchedEffect(extractedText) {
        if (extractedText.isNotEmpty()) {
            val cacheManager = CacheManager.getInstance(context)
            val cacheKey = cacheManager.cacheKey(extractedText)

            val cachedWords = cacheManager.loadTranslatedWords(cacheKey)
            if (cachedWords != null) {
                translatedWords.clear()
                translatedWords.addAll(cachedWords)
            } else {
                val translationService = TranslationService()
                coroutineScope.launch {
                    translationService.translateText(
                        text = extractedText,
                        targetLanguage = targetLanguage.value,
                        onWordsReceived = { words ->
                            translatedWords.clear()
                            translatedWords.addAll(words)
                            cacheManager.saveTranslatedWords(words, cacheKey)
                        },
                        extractedText = extractedText
                    )
                }
            }
        }
    }

    // Display PDF
    AndroidView<PDFView>(
        factory = { context ->
            PDFView(context, null).apply {

                // Set a background color to visualize the boundaries
                setBackgroundColor(1) // Black

                // Use ContentResolver to open an InputStream from the URI
                val inputStream = context.contentResolver.openInputStream(documentUri)
                if (inputStream != null) {
                    fromStream(inputStream)
                        .enableSwipe(true)
                        .enableDoubletap(true)
                        .defaultPage(0)
                        .fitEachPage(true) // Adjust rendering settings
                        .autoSpacing(false) // Disable auto-spacing
                        .load()
                } else {
                    Log.e("PDFView", "Failed to open input stream for URI: $documentUri")
                }
            }
        },
        modifier = modifier.fillMaxSize()
    )
}

// Function to extract text from a PDF file
private fun extractTextFromPdf(inputStream: InputStream): String {
    // Implement text extraction logic here
    // For now, return an empty string
    return ""
}