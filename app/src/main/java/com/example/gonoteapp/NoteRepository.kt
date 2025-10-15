package com.example.gonoteapp

import androidx.core.os.bundleOf
import com.example.gonoteapp.model.Folder
import com.example.gonoteapp.model.Note
import androidx.fragment.app.setFragmentResult
object NoteRepository {

    interface OnDataChangeListener {
        fun onDataChanged()
    }

    private val notes = mutableListOf<Note>()
    private val folders = mutableListOf<Folder>()

    private var nextId = 1L
    private var nextFolderId = 1L

    init {
        val folder1 = Folder(nextFolderId++, "Personal")
        val folder2 = Folder(nextFolderId++, "Work")
        folders.addAll(listOf(folder1, folder2))

        val initialNotes = listOf(
            Note(nextId++, folder2.id,"Meeting Notes", "Discuss Q3 budget and project timelines.", System.currentTimeMillis()),
            Note(nextId++, folder1.id, "Shopping List", "Milk, Bread, Eggs, Coffee.", System.currentTimeMillis()),
            Note(nextId++, folder1.id, "Book Ideas", "A story about a time-traveling librarian.", System.currentTimeMillis()),
            Note(nextId++, 0L, "Workout Plan", "Monday: Chest, Tuesday: Back, Wednesday: Legs.", System.currentTimeMillis())
        )
        notes.addAll(initialNotes)
    }

    fun getAllFolders(): List<Folder> = folders.toList()

    fun addFolder(name: String) {
        folders.add(Folder(nextFolderId++, name))
    }

    fun addNote(title: String, content: String) {
        val newNote = Note(
            id = nextId++,
            folderId = 0L,
            title = title,
            content = content,
            timestamp = System.currentTimeMillis()
        )
        notes.add(0, newNote)
    }

    fun getAllNotes(): List<Note> = notes.toList()

    fun getNoteById(id: Long): Note? {
        return notes.find { it.id == id }
    }

    fun getNotesForFolder(folderName: String): List<Note> {
        val folder = folders.find { it.name == folderName }
        return if (folder != null) {
            notes.filter { it.folderId == folder.id }.toList()
        } else {
            emptyList()
        }
    }

    fun updateNote(id: Long, newTitle: String, newContent: String) {
        val noteToUpdate = getNoteById(id)
        noteToUpdate?.let {
            it.title = newTitle
            it.content = newContent
            it.timestamp = System.currentTimeMillis()
        }


    }
}
