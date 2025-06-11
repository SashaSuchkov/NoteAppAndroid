package com.example.noteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.noteapp.data.NoteEntity
import com.example.noteapp.ui.theme.NoteAppTheme
import com.example.noteapp.viewmodel.NoteViewModel
import com.example.noteapp.viewmodel.NoteViewModelFactory
import androidx.compose.ui.Alignment

class EditNoteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Отримуємо ID редагованої нотатки
        val noteId = intent.getIntExtra("note_id", -1)

        setContent {
            NoteAppTheme {
                val noteViewModel: NoteViewModel = viewModel(
                    factory = NoteViewModelFactory(application)
                )
                EditNoteScreen(
                    noteId = noteId,
                    viewModel = noteViewModel,
                    onNoteUpdated = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(
    noteId: Int,
    viewModel: NoteViewModel,
    onNoteUpdated: () -> Unit
) {
    // Спершу завантажуємо існуючу нотатку
    val noteEntity by produceState<NoteEntity?>(initialValue = null, noteId) {
        value = viewModel.getNoteById(noteId)
    }

    // Локальні стани полів, оновлюються після завантаження
    var title by remember(noteEntity) { mutableStateOf(noteEntity?.title.orEmpty()) }
    var content by remember(noteEntity) { mutableStateOf(noteEntity?.content.orEmpty()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Редагувати нотатку") },
                actions = {
                    IconButton(
                        onClick = {
                            // Зберігаємо зміни
                            noteEntity?.let { original ->
                                val updated = original.copy(
                                    title = title.trim(),
                                    content = content.trim(),
                                    timestamp = System.currentTimeMillis()
                                )
                                viewModel.updateNote(updated)
                                onNoteUpdated()
                            }
                        },
                        enabled = noteEntity != null && (title.isNotBlank() || content.isNotBlank())
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                }
            )
        }
    ) { innerPadding ->
        noteEntity?.let {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Заголовок") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Вміст") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    maxLines = 10
                )
            }
        } ?: Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}