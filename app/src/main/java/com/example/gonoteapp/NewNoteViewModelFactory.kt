package com.example.gonoteapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Factory class to create an instance of NewNoteViewModel with a NoteRepository dependency.
 */
class NewNoteViewModelFactory(private val repository: NoteRepository) : ViewModelProvider.Factory {

    /**
     * Creates a new instance of the given `Class`.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewNoteViewModel::class.java)) {
            // If the requested ViewModel is of type NewNoteViewModel,
            // create an instance and pass the repository to its constructor.
            return NewNoteViewModel(repository) as T
        }
        // Otherwise, throw an exception.
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
