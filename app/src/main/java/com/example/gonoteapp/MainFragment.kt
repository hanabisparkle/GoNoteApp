package com.example.gonoteapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gonoteapp.model.Note

class MainFragment : Fragment(), OnNoteClickListener {

    private lateinit var notesRecyclerView: RecyclerView
    private lateinit var noteAdapter: NotePreviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val createButton: Button = view.findViewById(R.id.createbutton)

        // Find the RecyclerView from the inflated view
        notesRecyclerView = view.findViewById(R.id.notes_recycler_view)

        // 1. Create the data
        val initialNotes = getHardcodedNotes()

        // 2. Create the adapter and give it the data
        noteAdapter = NotePreviewAdapter(this)
        noteAdapter.setData(initialNotes)

        // 3. Set up the RecyclerView with the adapter and layout manager
        setupRecyclerView()

        createButton.setOnClickListener {
            val fragment = NewNote()
            parentFragmentManager.beginTransaction()
                .replace(R.id.my_fragment_container, fragment)
                .addToBackStack(null)
                .commit()

            // redirect to a fragment that lets user enter note contents and title
            // fragment should contain a back/cancel button
            // and a confirm new note button which adds a new note that will then show itself on the main menu
        }
    }

    override fun onNoteClicked(note: Note) {
        val fragment = NoteFullViewFragment()

        val bundle = Bundle()
        bundle.putLong("NOTE_ID", note.id)
        bundle.putString("NOTE_TITLE", note.title)
        bundle.putString("NOTE_CONTENT", note.content)
        bundle.putLong("NOTE_TIMESTAMP", note.timestamp)
        fragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.my_fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setupRecyclerView() {
        notesRecyclerView.adapter = noteAdapter
        notesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun getHardcodedNotes(): List<Note> {
        return listOf(
            Note(1, "Meeting Notes", "Discuss Q3 budget and project timelines.", System.currentTimeMillis()),
            Note(2, "Shopping List", "Milk, Bread, Eggs, Coffee.", System.currentTimeMillis()),
            Note(3, "Book Ideas", "A story about a time-traveling librarian.", System.currentTimeMillis()),
            Note(4, "Workout Plan", "Monday: Chest, Tuesday: Back, Wednesday: Legs.", System.currentTimeMillis())
        )
    }
}
