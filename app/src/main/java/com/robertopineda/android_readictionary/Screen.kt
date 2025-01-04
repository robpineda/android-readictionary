package com.robertopineda.android_readictionary

sealed class Screen(val route: String) {
    object DocumentList : Screen("documentList")
    object ReadingView : Screen("readingView/{documentUri}") {
        fun createRoute(documentUri: String) = "readingView/$documentUri"
    }
}