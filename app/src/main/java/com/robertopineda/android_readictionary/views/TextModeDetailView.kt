package com.robertopineda.android_readictionary.views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robertopineda.android_readictionary.models.Language
import com.robertopineda.android_readictionary.models.TextRecord
import com.robertopineda.android_readictionary.models.TranslatedWord
import com.robertopineda.android_readictionary.utilities.CacheManager
import com.robertopineda.android_readictionary.utilities.TranslationService
import kotlinx.coroutines.launch

@Composable
fun TextModeDetailView(
    record: TextRecord,
    translatedWords: SnapshotStateList<TranslatedWord>,
    targetLanguage: MutableState<Language>
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // State for the sheet's height
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    var dictionaryViewHeight by remember { mutableStateOf(configuration.screenHeightDp.dp * 3/4 ) }

    // Access LocalDensity in a composable context
    val density = LocalDensity.current

    // State for highlighted word
    var highlightedWordIndex by remember { mutableStateOf(0) }

    // LazyListState for DictionaryView scrolling
    val dictionaryListState = rememberLazyListState()

    // State to store the TextLayoutResult
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    // Function to highlight a word in the TextView
    fun highlightText(text: String, highlightedWord: String): AnnotatedString {
        return buildAnnotatedString {
            append(text)
            val startIndex = text.indexOf(highlightedWord)
            if (startIndex != -1) {
                addStyle(
                    style = SpanStyle(color = Color(0xFFFFA500)), // Orange
                    start = startIndex,
                    end = startIndex + highlightedWord.length
                )
            }
        }
    }

    // Function to scroll the DictionaryView to the corresponding word
    fun scrollDictionaryViewToWord(index: Int) {
        coroutineScope.launch {
            dictionaryListState.scrollToItem(index)
        }
    }

    // Function to find the word at a specific offset in the TextView
    fun findWordAtOffset(offset: Int, text: String, translatedWords: List<TranslatedWord>): TranslatedWord? {
        var currentIndex = 0
        var wordIndex = 0

        // Iterate through the full text (including punctuation)
        while (currentIndex < text.length && wordIndex < translatedWords.size) {
            val word = translatedWords[wordIndex]
            val wordLength = word.originalText.length

            // Skip non-word characters (e.g., punctuation) until we find the start of the current word
            while (currentIndex < text.length && !text.startsWith(word.originalText, currentIndex)) {
                currentIndex++
            }

            // Check if the current character is part of the current word
            if (offset >= currentIndex && offset < currentIndex + wordLength) {
                return word
            }

            // Move to the next word
            currentIndex += wordLength
            wordIndex++
        }

        return null
    }

    // Load cached data when the screen is first composed
    LaunchedEffect(Unit) {
        translatedWords.clear()
        val cacheManager = CacheManager.getInstance(context)
        val cacheKey = cacheManager.getCacheKeyForTranslations(record.text)

        // Check if cached data exists
        val cachedWords = cacheManager.loadTranslatedWords(cacheKey)
        if (cachedWords != null && cachedWords.isNotEmpty()) {
            translatedWords.addAll(cachedWords)
            Log.d("TextModeDetailView", "Loaded cached words: ${translatedWords.size}")
        } else {
            // No cached data, call the API
            val translationService = TranslationService(cacheManager)
            coroutineScope.launch {
                translationService.translateText(
                    text = record.text,
                    targetLanguage = targetLanguage.value,
                    translatedWords = translatedWords,
                    onWordsReceived = { words ->
                        translatedWords.addAll(words)
                        Log.d("TextModeDetailView", "Translated words: ${translatedWords.size}")
                    },
                    onStreamComplete = {
                        // Save the translated words to cache after the stream is complete
                        cacheManager.saveTranslatedWords(translatedWords, cacheKey)
                        Log.d("PDFView", "Saved words to cache: ${translatedWords.size}")
                    }
                )
            }


        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Text View (Top View)
        SelectionContainer {
            Text(
                text = highlightText(record.text, translatedWords.getOrNull(highlightedWordIndex)?.originalText ?: ""),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val characterOffset = textLayoutResult?.getOffsetForPosition(offset) ?: 0
                            val tappedWord = findWordAtOffset(characterOffset, record.text, translatedWords)
                            if (tappedWord != null) {
                                val index = translatedWords.indexOf(tappedWord)
                                if (index != -1) {
                                    highlightedWordIndex = index
                                    scrollDictionaryViewToWord(index)
                                }
                            }
                        }
                    },
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 20.sp
                ),
                onTextLayout = { textLayoutResult = it }
            )
        }

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
                        dictionaryViewHeight = newHeightDp.coerceIn(100.dp, screenHeight * 3 / 4)
                    }
                }
        ) {
            DictionaryView(
                translatedWords = translatedWords,
                highlightedWord = translatedWords.getOrNull(highlightedWordIndex)?.originalText,
                listState = dictionaryListState,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}