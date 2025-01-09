package com.robertopineda.android_readictionary.utilities

import android.content.Context
import android.net.Uri
import android.util.Log
import com.robertopineda.android_readictionary.models.TextRecord
import com.robertopineda.android_readictionary.models.TranslatedWord
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
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

    fun getCacheKeyForTranslations(text: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(text.toByteArray(UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    // Generate a cache key using the Uri and a hash of the text
    fun getCacheKeyForDocument(uri: Uri, text: String): String {
        val uriHash = uri.hashCode().toString()
        val textHash = getCacheKeyForTranslations(text)
        return "$uriHash-$textHash" // Combine Uri hash and text hash
    }

    // Generate a cache key based on name and text
    fun getCacheKeyForTextRecord(name: String, text: String): String {
        val combined = "$name:$text"
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(combined.toByteArray(UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    // Save a TextRecord to cache
    fun saveTextRecord(record: TextRecord, cacheKey: String) {
        val cacheFile = File(cacheDir, "$cacheKey.json")
        val jsonString = Json.encodeToString(TextRecord.serializer(), record)
        cacheFile.writeText(jsonString)
    }

    // Load all cached TextRecords
    fun loadCachedTextRecords(): List<TextRecord>? {
        val cacheFiles = cacheDir.listFiles { file -> file.name.endsWith(".json") }
        return cacheFiles?.mapNotNull { file ->
            try {
                val fileText = file.readText()
                // Try parsing as a single object first
                try {
                    val singleRecord = Json.decodeFromString(TextRecord.serializer(), fileText)
                    listOf(singleRecord)
                } catch (e: Exception) {
                    // If parsing as a single object fails, try parsing as a list
                    Json.decodeFromString(ListSerializer(TextRecord.serializer()), fileText)
                }
            } catch (e: Exception) {
                Log.e("CacheManager", "Error decoding JSON from file ${file.name}", e)
                null
            }
        }?.flatten()
    }

    // Delete a cached TextRecord
    fun deleteCachedTextRecord(cacheKey: String) {
        val cacheFile = File(cacheDir, "$cacheKey.json")
        if (cacheFile.exists()) {
            cacheFile.delete()
        }
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

    fun deleteCachedWords(cacheKey: String) {
        val cacheFile = File(cacheDir, "$cacheKey.json")
        if (cacheFile.exists()) {
            cacheFile.delete()
            Log.d("CacheManager", "Deleted cache for key: $cacheKey")
        }
    }
}