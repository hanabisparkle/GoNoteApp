// Create a new file, e.g., GoNoteApplication.kt
package com.example.gonoteapp

import android.app.Application

class GoNoteApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // This initializes the repository as soon as the app starts.
        NoteRepository.getInstance(this)
    }
}
