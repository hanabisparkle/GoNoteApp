package com.example.gonoteapp

import android.util.Log
import androidx.core.os.bundleOf
import com.example.gonoteapp.model.Folder
import com.example.gonoteapp.model.Note
import androidx.fragment.app.setFragmentResult
object NoteRepository {

    interface OnDataChangeListener {
        fun onDataChanged()
    }

    private const val NOTE_REPOSITORY = "NOTE_REPOSITORY"
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

    fun getAllFolders(): List<Folder> {
        for (f in folders) {
            f.noteCount = notes.count { it.folderId == f.id }
            Log.d(NOTE_REPOSITORY, "getAllFolders() -> Folder Name: ${f.name}, Note Count: ${f.noteCount}")
        }
        return folders.toList()
    }

    fun addFolder(name: String) {
        folders.add(Folder(nextFolderId++, name))
        Log.d(NOTE_REPOSITORY, "addFolder() -> Folder Name: ${name}")
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
        Log.d(NOTE_REPOSITORY, "getAllFolders()")
    }

    fun getAllNotes(): List<Note> {
        Log.d(NOTE_REPOSITORY, "getAllNotes()")
        return notes.toList()
    }

    fun getNoteById(id: Long): Note? {
        Log.d(NOTE_REPOSITORY, "getNoteById()")
        return notes.find { it.id == id }
    }

    fun getNotesForFolder(folderName: String): List<Note> {
        val folder = folders.find { it.name == folderName }
        Log.d(NOTE_REPOSITORY, "getNotesForFolder()")
        return if (folder != null) {
            Log.d(NOTE_REPOSITORY, "getNotesForFolder() -> There are notes in this folder")
            notes.filter { it.folderId == folder.id }.toList()
        } else {
            Log.d(NOTE_REPOSITORY, "getNotesForFolder() ->  This folder is empty")

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
        Log.d(NOTE_REPOSITORY, "updateNote() -> Note has been updated")
    }
}
