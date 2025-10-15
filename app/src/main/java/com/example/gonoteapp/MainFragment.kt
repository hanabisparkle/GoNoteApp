package com.example.gonoteapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gonoteapp.model.Note
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainFragment : Fragment(), OnNoteClickListener {

    // DISPLAY NOTES (1) recyclerview and adapter
    private lateinit var notesRecyclerView: RecyclerView
    private lateinit var noteAdapter: NotePreviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Listener for NEW notes
        setFragmentResultListener("new_note_request") { requestKey, bundle ->
            val newTitle = bundle.getString("note_title_key") ?: "Untitled"
            val newContent = bundle.getString("note_content_key") ?: ""

            // Use the repository to add the note
            NoteRepository.addNote(newTitle, newContent)
            // No need to update adapter here, onResume will do it
        }

        // Listener for EDITED notes
        setFragmentResultListener("note_edited_request") { requestKey, bundle ->
            val noteId = bundle.getLong("edited_id_key")
            val newTitle = bundle.getString("edited_title_key") ?: "Untitled"
            val newContent = bundle.getString("edited_content_key") ?: ""

            // Use the repository to update the note
            NoteRepository.updateNote(noteId, newTitle, newContent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val createButton: FloatingActionButton = view.findViewById(R.id.createbutton)

        // DISPLAY NOTES (2) find id to assign to recyclerview
        notesRecyclerView = view.findViewById(R.id.notes_recycler_view)

        // DISPLAY NOTES (3) initialize adapter (it will be empty at first)
        noteAdapter = NotePreviewAdapter(this)
        setupRecyclerView()

        createButton.setOnClickListener {
            showCreateDialog()
        }
    }


    override fun onResume() {
        super.onResume()
        // Every time the fragment becomes visible, refresh the list from the single source of truth.
        // This ensures new notes, updated notes, and deleted notes are always reflected.
        noteAdapter.setData(NoteRepository.getAllNotes())
    }

    override fun onNoteClicked(note: Note) {
        val fragment = NoteFullViewFragment()
        // Pass the Note's ID, which is all the next fragment needs
        fragment.arguments = bundleOf("NOTE_ID" to note.id)

        parentFragmentManager.beginTransaction()
            .replace(R.id.my_fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    // DISPLAY NOTES (4):
        // assigns adapter
        // layout manager for displaying items
        // itemtouchhelper for swipe to delete function
    private fun setupRecyclerView() {
        notesRecyclerView.adapter = noteAdapter
        notesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false // We don't want to handle move gestures
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val noteToDelete = noteAdapter.getNoteAt(position)

                showDeleteConfirmationDialog(noteToDelete)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(notesRecyclerView)
    }

    private fun showDeleteConfirmationDialog(note: Note) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Delete") { _, _ ->
                NoteRepository.deleteNote(note.id)
                noteAdapter.setData(NoteRepository.getAllNotes())
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                // If canceled, refresh the adapter to bring the item back
                noteAdapter.notifyDataSetChanged()
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun showCreateDialog() {
        val options = arrayOf("New Note", "New Folder")
        AlertDialog.Builder(requireContext())
            .setTitle("Create")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        // Navigate to NewNoteFragment for creating a new note
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.my_fragment_container, NewNoteFragment())
                            .addToBackStack(null)
                            .commit()
                    }
                    1 -> {
                        // "New Folder" option
                        Toast.makeText(requireContext(), "New folder created (not really)", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .show()
    }
}
