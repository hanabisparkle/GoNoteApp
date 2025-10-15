package com.example.gonoteapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FolderListFragment : Fragment() {
    // DISPLAY FOLDERS (1) recyclerview and adapter
    private lateinit var foldersRecyclerView: RecyclerView
    private lateinit var foldersAdapter: FolderAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // DISPLAY FOLDERS (2) find id to assign to recyclerview
        foldersRecyclerView = view.findViewById(R.id.folders_recycler_view)

        // DISPLAY FOLDERS (3) initialize adapter (it will be empty at first)
        foldersAdapter = FolderAdapter()
        setupRecyclerView()
    }

    // DISPLAY FOLDERS (4):
    // assigns adapter
    // layout manager for displaying items
    private fun setupRecyclerView() {
        foldersRecyclerView.adapter = foldersAdapter
        foldersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_folder_list, container, false)
    }
}