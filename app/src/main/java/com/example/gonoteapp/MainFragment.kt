package com.example.gonoteapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gonoteapp.model.Note

class MainFragment : Fragment() {

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

        // Find the RecyclerView from the inflated view
        notesRecyclerView = view.findViewById(R.id.notes_recycler_view)

        // 1. Create the data
        val initialNotes = getHardcodedNotes()

        // 2. Create the adapter and give it the data
        noteAdapter = NotePreviewAdapter()
        noteAdapter.setData(initialNotes)

        // 3. Set up the RecyclerView with the adapter and layout manager
        setupRecyclerView()
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
