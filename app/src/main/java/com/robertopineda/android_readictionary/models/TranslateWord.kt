package com.robertopineda.android_readictionary.models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class TranslatedWord(
    val id: String = UUID.randomUUID().toString(),
    val originalText: String,
    val transliteration: String?,
    val romaji: String?,
    val definitions: List<String>
)

enum class Language {
    JAPANESE, KOREAN, SPANISH, ENGLISH
}