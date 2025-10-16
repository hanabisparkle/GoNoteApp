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

    fun addNote(title: String, content: String, folderName: String? = null) {
        val folderId = folderName?.let { name ->
            folders.find { it.name == name }?.id
        } ?: 0L

        val newNote = Note(
            id = nextId++,
            folderId = folderId,
            title = title,
            content = content,
            timestamp = System.currentTimeMillis()
        )
        notes.add(0, newNote)
        Log.d(NOTE_REPOSITORY, "addNote() -> Note added to folderId: $folderId")
    }

    fun updateFolder(id: Long, newName: String) {
        val folderToUpdate = getFolderById(id)
        folderToUpdate?.let {
            it.name = newName
        }
        Log.d(NOTE_REPOSITORY, "updateFolder() -> Folder has been updated")
    }

    fun deleteFolder(id: Long) {
        val folderToDelete = getFolderById(id)
        folderToDelete?.let {
            folders.remove(it)
        }
        Log.d(NOTE_REPOSITORY, "deleteFolder() -> Folder has been deleted")
    }

    fun getAllNotes(): List<Note> {
        Log.d(NOTE_REPOSITORY, "getAllNotes()")
        return notes.toList()
    }

    fun getNoteById(id: Long): Note? {
        Log.d(NOTE_REPOSITORY, "getNoteById()")
        return notes.find { it.id == id }
    }

    fun getFolderById(id: Long): Folder? {
        Log.d(NOTE_REPOSITORY, "getFolderById()")
        return folders.find { it.id == id }
    }

    fun getNotesForFolder(folderId: Long): List<Note> {
        val folder = folders.find { it.id == folderId }
        Log.d(NOTE_REPOSITORY, "getNotesForFolder()")
        return if (folder != null) {
            Log.d(NOTE_REPOSITORY, "getNotesForFolder() -> There are notes in this folder")
            notes.filter { it.folderId == folder.id }.toList()
        } else {
            Log.d(NOTE_REPOSITORY, "getNotesForFolder() ->  This folder is empty")
            emptyList()
        }
    }

    fun updateNote(id: Long, newTitle: String, newContent: String, folderName: String? = null) {
        val noteToUpdate = getNoteById(id)
        noteToUpdate?.let {
            it.title = newTitle
            it.content = newContent
            it.timestamp = System.currentTimeMillis()
            folderName?.let { name ->
                val folder = folders.find { f -> f.name == name }
                if (folder != null) {
                    it.folderId = folder.id
                }
            }
        }
        Log.d(NOTE_REPOSITORY, "updateNote() -> Note has been updated")
    }

    fun deleteNote(id: Long) {
        val noteToDelete = getNoteById(id)
        noteToDelete?.let {
            notes.remove(it)
        }
        Log.d(NOTE_REPOSITORY, "deleteNote() -> Note has been deleted")
    }
}
