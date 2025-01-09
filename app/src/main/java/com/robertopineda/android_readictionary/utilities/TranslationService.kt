package com.robertopineda.android_readictionary.utilities

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.robertopineda.android_readictionary.models.Language
import com.robertopineda.android_readictionary.models.TranslatedWord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class TranslationService(private val cacheManager: CacheManager) {

    private val client = OkHttpClient()

    suspend fun translateText(
        text: String,
        targetLanguage: Language,
        translatedWords: SnapshotStateList<TranslatedWord>,
        onWordsReceived: (List<TranslatedWord>) -> Unit,
        onStreamComplete: () -> Unit
    ) = withContext(Dispatchers.IO) {

        val requestBody = JSONObject().apply {
            put("model", Config.apiModel)
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", Config.apiMessageContent)
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", text)
                })
            })
            put("stream", true)
        }.toString()

        val requestBodyMediaType = "application/json".toMediaType()
        val requestBodyOkHttp = requestBody.toRequestBody(requestBodyMediaType)

        val request = Request.Builder()
            .url(Config.apiUrl)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer ${Config.apiKey}")
            .post(requestBodyOkHttp)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.let { body ->
                        val buffer = body.source()
                        var accumulatedContent = "" // To accumulate the incremental content

                        while (!buffer.exhausted()) {
                            val line = buffer.readUtf8Line()
                            if (line != null && line.startsWith("data: ")) {
                                val jsonString = line.substring(6) // Remove "data: " prefix

                                // Check if the response is a control message (e.g., ["DONE"])
                                if (jsonString == "[DONE]") {
                                    Log.d("TranslationService", "Stream completed")
                                    continue
                                }

                                try {
                                    val json = JSONObject(jsonString)
                                    val choices = json.getJSONArray("choices")
                                    val delta = choices.getJSONObject(0).getJSONObject("delta")
                                    val content = delta.getString("content")
                                    accumulatedContent += content

                                    // If the content ends with "\n\n", it indicates the end of a word entry
                                    if (accumulatedContent.endsWith("\n\n")) {
                                        val newWords = parseTranslatedContent(accumulatedContent)
                                        onWordsReceived(newWords)
                                        accumulatedContent = "" // Reset the accumulated content
                                    }
                                } catch (e: JSONException) {
                                    Log.e("TranslationService", "Failed to parse JSON: $jsonString", e)
                                }
                            }
                        }

                        // Handle any remaining content
                        if (accumulatedContent.isNotEmpty()) {
                            val newWords = parseTranslatedContent(accumulatedContent)
                            onWordsReceived(newWords)
                            onStreamComplete()
                            accumulatedContent = ""
                        }
                    }
                }
                else{
                    val errorBody = response.body?.string()
                    Log.e("OkHttp", "Request failed with code: ${response.code}")
                    Log.e("OkHttp", "Error response body: $errorBody")
                }
            }
        })
    }

    private fun parseTranslatedContent(content: String): List<TranslatedWord> {
        val translatedWords = mutableListOf<TranslatedWord>()
        val lines = content.split("\n")

        var currentWordInfo: String? = null
        var currentDefinitions: String? = null

        for (line in lines) {
            if (line.isEmpty()) {
                // End of a word entry
                if (currentWordInfo != null && currentDefinitions != null) {
                    val word = parseWordEntry(currentWordInfo, currentDefinitions)
                    if (word != null) {
                        translatedWords.add(word)
                    }
                }
                currentWordInfo = null
                currentDefinitions = null
            } else if (currentWordInfo == null) {
                // Word info line
                currentWordInfo = line
            } else {
                // Definitions line
                currentDefinitions = line
            }
        }

        // Handle the last word entry
        if (currentWordInfo != null && currentDefinitions != null) {
            val word = parseWordEntry(currentWordInfo, currentDefinitions)
            if (word != null) {
                translatedWords.add(word)
            }
        }

        return translatedWords
    }

    private fun parseWordEntry(wordInfo: String, definitions: String): TranslatedWord? {

        val normalizedWordInfo = wordInfo
            .replace("、", ",") // Japanese comma
            .replace("،", ",") // Arabic comma
            .replace("，", ",") // Chinese comma
            .replace("՝", ",") // Armenian comma
            .replace("᠂", ",") // Mongolian comma
            .replace("፣", ",") // Ethiopic comma
            .replace("ฯ", ",") // Thai comma
            .replace("་", ",") // Tibetan comma
            .replace("។", ",") // Khmer comma
        val wordInfoComponents = normalizedWordInfo.split(Regex(",\\s*"))

        // Split by any type of comma and optional whitespace
//        val wordInfoComponents = wordInfo.split(Regex("""[,\u3001\u060C\uFF0C\u055D\u1802\u1363\u0E2F\u0F0B\u17D4]"""))
//            .map { it.trim() } // Trim whitespace from each component
        if (wordInfoComponents.size < 3) return null

        val originalText = wordInfoComponents[0]
        var transliteration = wordInfoComponents[1]
        val romaji = wordInfoComponents[2]

        // Remove repeated "[original text], [original text]" for languages other than Japanese
        if (originalText == transliteration) {
            transliteration = ""
        }

        // Parse definitions
        val definitionsArray = definitions.split(", ")
            .map { it.replace(Regex("^[0-9]+\\. "), "") }

        return TranslatedWord(
            originalText = originalText,
            transliteration = transliteration,
            romaji = romaji,
            definitions = definitionsArray
        )
    }
}