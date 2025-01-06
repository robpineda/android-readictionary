package com.robertopineda.android_readictionary.views

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

@Composable
fun DocumentPicker(onFilePicked: (Uri) -> Unit) {
    val context = LocalContext.current

    // Create an intent to open documents
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "*/*"
        putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/pdf", "application/epub+zip"))
    }

    // Launcher for picking a document
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                // Take persistable URI permissions
                val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, flags)
                onFilePicked(uri) // Notify the caller that a file was picked
            }
        }
    }

    // Launch the document picker
    LaunchedEffect(Unit) {
        launcher.launch(intent)
    }
}