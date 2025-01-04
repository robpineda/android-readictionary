package com.robertopineda.android_readictionary.models

import java.util.UUID

data class TextRecord(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val text: String,
    var translatedWords: List<TranslatedWord>
)