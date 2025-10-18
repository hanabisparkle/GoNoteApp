package com.example.gonoteapp.fragments

import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gonoteapp.NotePreviewAdapter
import com.example.gonoteapp.NoteRepository
import com.example.gonoteapp.OnNoteClickListener
import com.example.gonoteapp.R
import com.example.gonoteapp.model.Note

const val BASE_NOTE_LIST_FRAGMENT = "BaseNoteListFragment.kt"

abstract class BaseNoteListFragment : Fragment(), OnNoteClickListener, NoteRepository.OnDataChangeListener {
    protected lateinit var notesRecyclerView: RecyclerView
    protected lateinit var noteAdapter: NotePreviewAdapter
    private val selectedNotes = mutableSetOf<Note>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Listener untuk pembuatan note baru oleh user
        setFragmentResultListener("new_note_request") { _, bundle ->
            val newTitle = bundle.getString("note_title_key") ?: "Untitled"
            val newContent = bundle.getString("note_content_key") ?: ""
            NoteRepository.addNote(newTitle, newContent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_base_note_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notesRecyclerView = view.findViewById(R.id.notes_recycler_view)
        noteAdapter = NotePreviewAdapter(this)
        setupRecyclerView()
        loadNotes()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDataChanged() {
        loadNotes()
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
    }

    override fun onNoteClicked(note: Note) {
        val fragment = NoteFullViewFragment()
        fragment.arguments = bundleOf("NOTE_ID" to note.id)

        parentFragmentManager.beginTransaction()
            .replace(R.id.my_fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onNoteSelected(note: Note, isSelected: Boolean) {
        if (isSelected) {
            selectedNotes.add(note)
            Log.d(BASE_NOTE_LIST_FRAGMENT, "onNoteSelected() -> Note added to selected list")
        } else {
            selectedNotes.remove(note)
            Log.d(BASE_NOTE_LIST_FRAGMENT, "onNoteSelected() -> Note removed from selected list")
        }
    }

    fun getSelectedNotes() : Set<Note> {
        Log.d(BASE_NOTE_LIST_FRAGMENT, "getSelectedNotes() -> Selected ${selectedNotes.size} notes")
        return selectedNotes
    }

    private fun setupRecyclerView() {
        notesRecyclerView.adapter = noteAdapter
        notesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val note = noteAdapter.getNoteAt(position)

                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        showDeleteConfirmationDialog(note, viewHolder)
                    }
                    ItemTouchHelper.RIGHT -> {
                        val fragment = NewNoteFragment()
                        fragment.arguments = bundleOf("NOTE_ID_TO_EDIT" to note.id)

                        parentFragmentManager.beginTransaction()
                            .replace(R.id.my_fragment_container, fragment)
                            .addToBackStack(null)
                            .commit()
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView
                    val editLayout = itemView.findViewById<View>(R.id.edit_action_layout)
                    val deleteLayout = itemView.findViewById<View>(R.id.delete_action_layout)
                    val cardView = itemView.findViewById<View>(R.id.note_card)

                    // Show the correct background
                    when {
                        dX > 0 -> { // Swiping right
                            editLayout.visibility = View.VISIBLE
                            deleteLayout.visibility = View.GONE
                        }
                        dX < 0 -> { // Swiping left
                            editLayout.visibility = View.GONE
                            deleteLayout.visibility = View.VISIBLE
                        }
                        else -> { // Not swiped
                            editLayout.visibility = View.GONE
                            deleteLayout.visibility = View.GONE
                        }
                    }

                    // Manually move only the card on top, leaving the background stationary.
                    cardView.translationX = dX

                } else {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                // This is called when a swipe is cancelled or completed.
                super.clearView(recyclerView, viewHolder)
                val itemView = viewHolder.itemView
                // Reset the translation of the card
                itemView.findViewById<View>(R.id.note_card).translationX = 0f

                // Hide the backgrounds
                itemView.findViewById<View>(R.id.edit_action_layout).visibility = View.GONE
                itemView.findViewById<View>(R.id.delete_action_layout).visibility = View.GONE
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(notesRecyclerView)
    }

    private fun showDeleteConfirmationDialog(note: Note, viewHolder: RecyclerView.ViewHolder) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Delete") { _, _ ->
                NoteRepository.deleteNote(note.id)
                noteAdapter = NotePreviewAdapter(this)
                notesRecyclerView.adapter = noteAdapter
                loadNotes()
            }
            .setNegativeButton("Cancel") { _, _ ->
                noteAdapter.notifyItemChanged(viewHolder.adapterPosition)
            }
            .setOnCancelListener {
                noteAdapter.notifyItemChanged(viewHolder.adapterPosition)
            }
            .create()
            .show()
    }

    protected abstract fun loadNotes()
}
