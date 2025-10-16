package com.example.gonoteapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gonoteapp.model.Note

class NewNoteViewModel : ViewModel() {

    private var noteIdToEdit: Long = -1L
    val isEditMode: Boolean
        get() = noteIdToEdit != -1L


    private val _note = MutableLiveData<Note?>()
    val note: LiveData<Note?> = _note

    private val _navigateBack = MutableLiveData<Boolean>()
    val navigateBack: LiveData<Boolean> = _navigateBack

    fun loadNote(noteId: Long) {
        if (noteId == -1L) return
        noteIdToEdit = noteId

        _note.value = NoteRepository.getNoteById(noteId)
    }

    fun saveNote(title: String, content: String, folderName: String? = null) {
        if (isEditMode) {
            NoteRepository.updateNote(noteIdToEdit, title, content, folderName)
        } else {
            NoteRepository.addNote(title, content, folderName)
        }
        _navigateBack.value = true
    }

    fun onNavigateBackComplete() {
        _navigateBack.value = false
    }
}
