package com.example.noteapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.noteapp.data.NoteEntity
import com.example.noteapp.ui.theme.NoteAppTheme
import com.example.noteapp.viewmodel.NoteViewModel
import com.example.noteapp.viewmodel.NoteViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.Alignment

class ViewNoteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Отримаємо ID нотатки з Intent
        val noteId = intent.getIntExtra("note_id", -1)

        setContent {
            NoteAppTheme {
                val viewModel: NoteViewModel = viewModel(
                    factory = NoteViewModelFactory(application)
                )
                ViewNoteScreen(
                    noteId = noteId,
                    viewModel = viewModel,
                    onEdit = { id ->
                        startActivity(
                            Intent(this, EditNoteActivity::class.java)
                                .putExtra("note_id", id)
                        )
                        finish()
                    },
                    onDelete = { note ->
                        viewModel.deleteNote(note)
                        finish()
                    },
                    onBack = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewNoteScreen(
    noteId: Int,
    viewModel: NoteViewModel,
    onEdit: (Int) -> Unit,
    onDelete: (NoteEntity) -> Unit,
    onBack: () -> Unit
) {
    // Загрузка нотатки з бази
    val noteState by produceState<NoteEntity?>(initialValue = null, noteId) {
        value = viewModel.getNoteById(noteId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Перегляд нотатки") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (noteState != null) {
                        IconButton(onClick = { onEdit(noteState!!.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { onDelete(noteState!!) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        noteState?.let { note ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = note.title.ifBlank { "Без заголовка" },
                    style = MaterialTheme.typography.headlineMedium
                )
                val formattedDate = remember(note.timestamp) {
                    SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                        .format(Date(note.timestamp))
                }
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall
                )
                Divider()
                Text(
                    text = note.content.ifBlank { "Немає тексту" },
                    style = MaterialTheme.typography.bodyLarge
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