package com.robertopineda.android_readictionary.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TextModeInputView(onSave: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Translation") },
                actions = {
                    IconButton(
                        onClick = { onSave(name, text) },
                        enabled = name.isNotEmpty() && text.isNotEmpty()
                    ) {
                        Text("Translate")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Record Name") }
            )
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Text to Translate") },
                modifier = Modifier.height(200.dp)
            )
        }
    }
}