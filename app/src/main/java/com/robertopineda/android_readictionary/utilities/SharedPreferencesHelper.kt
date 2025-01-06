package com.robertopineda.android_readictionary.utilities

import android.content.Context
import android.net.Uri
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesHelper(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Save the list of URIs
    fun saveDocuments(documents: List<Uri>) {
        // Convert URIs to strings before saving
        val uriStrings = documents.map { it.toString() }
        val json = gson.toJson(uriStrings)
        sharedPreferences.edit {
            putString("documents", json)
        }
    }

    // Load the list of URIs
    fun loadDocuments(): List<Uri> {
        val json = sharedPreferences.getString("documents", null)
        return if (json != null) {
            // Convert strings back to URIs after loading
            val type = object : TypeToken<List<String>>() {}.type
            val uriStrings: List<String> = gson.fromJson(json, type) ?: emptyList()
            uriStrings.map { Uri.parse(it) }
        } else {
            emptyList()
        }
    }
}