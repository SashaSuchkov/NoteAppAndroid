package com.example.noteapp.model

data class Note(
    val id: Int,
    var title: String,
    var content: String,
    var timestamp: Long
)