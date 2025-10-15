package com.example.gonoteapp.model

data class Note(
    var id: Long,
    var folderId: Long = 0L,
    var title: String,
    var content: String,
    var timestamp: Long
)
