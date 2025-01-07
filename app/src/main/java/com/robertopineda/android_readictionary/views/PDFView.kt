package com.robertopineda.android_readictionary.views

import android.net.Uri
import android.os.ParcelFileDescriptor
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
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import java.io.FileOutputStream
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

            // Clear the translatedWords list before starting a new translation
            translatedWords.clear()
            Log.d("ReadingView", "Cleared translatedWords list")

            val cacheManager = CacheManager.getInstance(context)
            val cacheKey = cacheManager.cacheKey(extractedText)

            // Check if cached data exists
            val cachedWords = cacheManager.loadTranslatedWords(cacheKey)
            if (cachedWords != null) {
                translatedWords.clear()
                translatedWords.addAll(cachedWords)
                Log.d("ReadingView", "Loaded cached words: ${translatedWords.size}")
            } else {
                // No cached data, call the API
                val translationService = TranslationService(cacheManager)
                coroutineScope.launch {
                    translationService.translateText(
                        text = extractedText,
                        targetLanguage = targetLanguage.value,
                        translatedWords = translatedWords,
                        onWordsReceived = { words ->
                            translatedWords.addAll(words)
                            Log.d("ReadingView", "Translated words: ${translatedWords.size}")
                        }
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
                        .fitEachPage(true)
                        .autoSpacing(false)
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
    return try {
        val pdfDocument = PdfDocument(PdfReader(inputStream))
        val textBuilder = StringBuilder()
        for (i in 1..pdfDocument.numberOfPages) {
            val page = pdfDocument.getPage(i)
            val text = PdfTextExtractor.getTextFromPage(page)
            textBuilder.append(text)
        }
        pdfDocument.close()
        textBuilder.toString()
    } catch (e: Exception) {
        Log.e("PDFView", "Error extracting text from PDF: ${e.message}")
        "" // Return an empty string in case of an error
    }
}