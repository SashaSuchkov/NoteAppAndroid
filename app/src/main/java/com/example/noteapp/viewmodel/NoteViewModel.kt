package com.example.noteapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.data.NoteDatabase
import com.example.noteapp.data.NoteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val noteDao = NoteDatabase.getDatabase(application).noteDao()
    val allNotes: Flow<List<NoteEntity>> = noteDao.getAllNotesFlow()

    fun addNote(note: NoteEntity) {
        viewModelScope.launch {
            noteDao.insert(note)
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            noteDao.delete(note)
        }
    }

    fun updateNote(note: NoteEntity) {
        viewModelScope.launch {
            noteDao.update(note)
        }
    }

    suspend fun getNoteById(id: Int): NoteEntity = noteDao.getNoteById(id)
}