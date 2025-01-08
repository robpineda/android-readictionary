package com.robertopineda.android_readictionary

import android.net.Uri

sealed class Screen(val route: String) {
    object ContentView : Screen("contentView")
    object DocumentListView : Screen("documentListView")
    object TextModeListView : Screen("textModeListView")
    object ReadingView : Screen("readingView/{documentUri}") {
        fun createRoute(documentUri: String) = "readingView/${Uri.encode(documentUri)}"
    }
    object TextModeDetailView : Screen("textModeDetailView/{recordId}") {
        fun createRoute(recordId: String) = "textModeDetailView/$recordId"
    }
    object TextModeInputView : Screen("textModeInputView") // New route
}