package com.robertopineda.android_readictionary.views

import android.net.Uri
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

@Composable
fun PDFView(
    documentUri: Uri,
    translatedWords: SnapshotStateList<TranslatedWord>,
    targetLanguage: MutableState<Language>
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Extract text from the PDF
    var extractedText by remember { mutableStateOf("") }

    // Load PDF and extract text
    LaunchedEffect(documentUri) {
        withContext(Dispatchers.IO) {
            val pdfFile = File(documentUri.path ?: "")
            if (pdfFile.exists()) {
                extractedText = extractTextFromPdf(pdfFile)
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
                fromUri(documentUri)
                    .enableSwipe(true)
                    .enableDoubletap(true)
                    .defaultPage(0)
                    .load()
            }
        }
    )
}

// Function to extract text from a PDF file
private fun extractTextFromPdf(pdfFile: File): String {
    // Implement text extraction logic here
    // For now, return an empty string
    return ""
}