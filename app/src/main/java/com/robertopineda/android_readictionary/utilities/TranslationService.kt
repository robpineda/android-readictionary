package com.robertopineda.android_readictionary.utilities

import com.robertopineda.android_readictionary.models.Language
import com.robertopineda.android_readictionary.models.TranslatedWord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class TranslationService {

    private val client = OkHttpClient()

    suspend fun translateText(
        text: String,
        targetLanguage: Language,
        onWordsReceived: (List<TranslatedWord>) -> Unit,
        extractedText: String
    ) = withContext(Dispatchers.IO) {
        val requestBody = JSONObject().apply {
            put("model", Config.apiModel)
            put("messages", listOf(
                mapOf("role" to "system", "content" to Config.apiMessageContent),
                mapOf("role" to "user", "content" to text)
            ))
            put("stream", true)
        }.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(Config.apiUrl)
            .addHeader("Authorization", "Bearer ${Config.apiKey}")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.let { body ->
                        val buffer = body.source()
                        while (!buffer.exhausted()) {
                            val line = buffer.readUtf8Line()
                            if (line != null && line.startsWith("data: ")) {
                                val jsonString = line.substring(6)
                                val json = JSONObject(jsonString)
                                val choices = json.getJSONArray("choices")
                                val delta = choices.getJSONObject(0).getJSONObject("delta")
                                val content = delta.getString("content")
                                val words = parseTranslatedContent(content)
                                onWordsReceived(words)
                            }
                        }
                    }
                }
            }
        })
    }

    private fun parseTranslatedContent(content: String): List<TranslatedWord> {
        // Implement parsing logic here
        return emptyList()
    }
}