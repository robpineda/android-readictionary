package com.robertopineda.android_readictionary.utilities

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

fun getFileNameFromUri(context: Context, uri: Uri): String? {
    val contentResolver: ContentResolver = context.contentResolver
    val cursor = contentResolver.query(uri, null, null, null, null)
    return cursor?.use {
        if (it.moveToFirst()) {
            val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (displayNameIndex >= 0) {
                it.getString(displayNameIndex)
            } else {
                // Fallback: Use the last path segment if DISPLAY_NAME is not available
                uri.lastPathSegment
            }
        } else {
            null
        }
    }
}