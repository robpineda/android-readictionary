package com.robertopineda.android_readictionary.utilities

import android.content.Context
import com.robertopineda.android_readictionary.models.TranslatedWord
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer
import java.io.File
import java.security.MessageDigest
import kotlin.text.Charsets.UTF_8

class CacheManager private constructor(context: Context) {

    private val cacheDir: File = File(context.cacheDir, "ReadictionaryCache")

    init {
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: CacheManager? = null

        fun getInstance(context: Context): CacheManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CacheManager(context).also { INSTANCE = it }
            }
        }
    }

    fun cacheKey(text: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(text.toByteArray(UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun saveTranslatedWords(words: List<TranslatedWord>, cacheKey: String) {
        val cacheFile = File(cacheDir, "$cacheKey.json")
        val jsonString = Json.encodeToString(ListSerializer(TranslatedWord.serializer()), words)
        cacheFile.writeText(jsonString)
    }

    fun loadTranslatedWords(cacheKey: String): List<TranslatedWord>? {
        val cacheFile = File(cacheDir, "$cacheKey.json")
        return if (cacheFile.exists()) {
            Json.decodeFromString(cacheFile.readText())
        } else {
            null
        }
    }
}