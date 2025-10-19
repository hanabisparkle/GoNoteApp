package com.example.gonoteapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import com.example.gonoteapp.MainActivity
import com.example.gonoteapp.NotePreviewAdapter
import com.example.gonoteapp.NoteRepository
import com.example.gonoteapp.R
import com.example.gonoteapp.model.Note

const val MAIN_FRAGMENT = "MainFragment.kt"
class MainFragment : BaseNoteListFragment() {

    private var isSelectionMode = false
    private lateinit var deleteButton: Button
    private lateinit var addToFolderButton: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selectButton: Button = view.findViewById(R.id.select_button)
        deleteButton = view.findViewById(R.id.delete_button)
        addToFolderButton = view.findViewById(R.id.add_to_folder_button)

        val searchView: SearchView = view.findViewById(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterNotes(newText)
                return true
            }
        })

        selectButton.setOnClickListener {
            isSelectionMode = !isSelectionMode
            noteAdapter.setSelectionMode(isSelectionMode)

            if (isSelectionMode) {
                deleteButton.visibility = View.VISIBLE
                addToFolderButton.visibility = View.VISIBLE
            } else {
                deleteButton.visibility = View.GONE
                addToFolderButton.visibility = View.GONE
            }
        }

        addToFolderButton.setOnClickListener {
            Log.d(MAIN_FRAGMENT, "addToFolderButton.setOnClickListener() -> Adding to folder")
            val selectedNotes = getSelectedNotes()
            if (selectedNotes.isNotEmpty()){
                showFolderSelectDialog(selectedNotes)
            }
        }

        deleteButton.setOnClickListener {
            val selectedNotes = getSelectedNotes()
            if (selectedNotes.isNotEmpty()) {
                showDeleteSelectedNotesDialog(selectedNotes)
            }
        }

    }

    private fun filterNotes(query: String?) {
        val allNotes = NoteRepository.getAllNotes()
        val notesToShow = if (query.isNullOrBlank()) {
            allNotes
        } else {
            allNotes.filter { note ->
                note.title.contains(query, ignoreCase = true) ||
                        note.content.contains(query, ignoreCase = true)
            }
        }
        noteAdapter.setData(notesToShow)
        updateEmptyViewVisibility(notesToShow, R.string.empty_notes)
    }


    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.updateTitle("Home")
    }

    override fun loadNotes() {
        val allNotes = NoteRepository.getAllNotes()
        noteAdapter.setData(allNotes)
        updateEmptyViewVisibility(allNotes, R.string.empty_notes)
    }

    private fun showDeleteSelectedNotesDialog(selectedNotes: Set<Note>){
        val options = arrayOf("Delete", "Cancel")
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete these notes?")
            .setPositiveButton("Delete") { _, _ ->
                for (note in selectedNotes){
                    NoteRepository.deleteNote(note.id)
                }
                notesRecyclerView.adapter = noteAdapter
                loadNotes()
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun showFolderSelectDialog(selectedNotes: Set<Note>) {
        val folders = NoteRepository.getAllFolders()
        if (folders.isNotEmpty()) {
            AlertDialog.Builder(requireContext())
                .setTitle("Select folder")
                .setItems(folders.map { it.name }.toTypedArray()) { _, which ->
                    val selectedFolder = folders[which]

                    for (note in selectedNotes) {
                        if (note.folderId != selectedFolder.id){
                            note.folderId = selectedFolder.id

                        } else {
                            Log.d(MAIN_FRAGMENT, "showFolderSelectDialog() -> Note already in folder")
                        }
                    }
                    Log.d(MAIN_FRAGMENT, "showFolderSelectDialog() -> Notes added to folder")
                }
                .show()
            Toast.makeText(requireContext(), "Notes added to folder", Toast.LENGTH_LONG).show()
        } else {
            Log.d(MAIN_FRAGMENT, "showFolderSelectDialog() -> No folders found")
            Toast.makeText(requireContext(), "No folders to add to. Create a folder first.", Toast.LENGTH_LONG).show()
        }
    }
}
