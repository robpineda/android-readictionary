package com.robertopineda.android_readictionary.models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class TextRecord(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val text: String,
    var translatedWords: List<TranslatedWord>
)