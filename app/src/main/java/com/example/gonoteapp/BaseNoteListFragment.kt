
package com.example.gonoteapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gonoteapp.model.Note

abstract class BaseNoteListFragment : Fragment(), OnNoteClickListener, NoteRepository.OnDataChangeListener {

    protected lateinit var notesRecyclerView: RecyclerView
    protected lateinit var noteAdapter: NotePreviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Listener for NEW notes
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
        // Inflate a generic layout with a RecyclerView
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

    private fun setupRecyclerView() {
        notesRecyclerView.adapter = noteAdapter
        notesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val noteToDelete = noteAdapter.getNoteAt(position)
                //showDeleteConfirmationDialog(noteToDelete)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(notesRecyclerView)
    }

//    private fun showDeleteConfirmationDialog(note: Note) {
//        AlertDialog.Builder(requireContext())
//            .setTitle("Delete Note")
//            .setMessage("Are you sure you want to delete this note?")
//            .setPositiveButton("Delete") { _, _ ->
//                NoteRepository.deleteNote(note.id)
//            }
//            .setNegativeButton("Cancel") { dialog, _ ->
//                noteAdapter.notifyDataSetChanged()
//                dialog.dismiss()
//            }
//            .create()
//            .show()
//    }

    protected abstract fun loadNotes()
}
