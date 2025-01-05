package com.robertopineda.android_readictionary

sealed class Screen(val route: String) {
    object DocumentList : Screen("documentList")
    object ReadingView : Screen("readingView/{documentUri}") {
        fun createRoute(documentUri: String) = "readingView/$documentUri"
    }
    object TextModeDetailView : Screen("textModeDetailView/{recordId}") {
        fun createRoute(recordId: String) = "textModeDetailView/$recordId"
    }
}