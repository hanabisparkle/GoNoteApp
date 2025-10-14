package com.example.gonoteapp

import com.example.gonoteapp.model.Note

// By declaring this as an 'object', Kotlin ensures there is only ever one instance of it.
// This is our central, in-memory "database".
object NoteRepository {

    // The master list of notes. It's private so it can only be changed via the methods below.
    private val notes = mutableListOf<Note>()

    // A variable to keep track of the next available ID.
    private var nextId = 1L

    init {
        // This block runs only once when the object is first created.
        // We pre-fill it with your hardcoded data.
        val initialNotes = listOf(
            Note(nextId++, "Meeting Notes", "Discuss Q3 budget and project timelines.", System.currentTimeMillis()),
            Note(nextId++, "Shopping List", "Milk, Bread, Eggs, Coffee.", System.currentTimeMillis()),
            Note(nextId++, "Book Ideas", "A story about a time-traveling librarian.", System.currentTimeMillis()),
            Note(nextId++, "Workout Plan", "Monday: Chest, Tuesday: Back, Wednesday: Legs.", System.currentTimeMillis())
        )
        notes.addAll(initialNotes)
    }

    // CREATE: Adds a new note to the list.
    fun addNote(title: String, content: String) {
        val newNote = Note(
            id = nextId++,
            title = title,
            content = content,
            timestamp = System.currentTimeMillis()
        )
        notes.add(0, newNote) // Add to the top of the list
    }

    // READ: Returns the full, current list of notes.
    fun getAllNotes(): List<Note> {
        return notes
    }

    // READ: Finds a single note by its ID.
    fun getNoteById(id: Long): Note? {
        return notes.find { it.id == id }
    }

    // UPDATE: Finds an existing note by its ID and updates its content.
    fun updateNote(id: Long, newTitle: String, newContent: String) {
        val noteToUpdate = getNoteById(id)
        noteToUpdate?.let {
            it.title = newTitle
            it.content = newContent
            it.timestamp = System.currentTimeMillis() // Update timestamp on edit
        }
    }

    // DELETE: Removes a note from the list by its ID.
    fun deleteNote(id: Long) {
        notes.removeAll { it.id == id }
    }
}
