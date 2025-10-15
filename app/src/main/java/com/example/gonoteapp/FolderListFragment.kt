package com.example.gonoteapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gonoteapp.model.Folder

class FolderListFragment : Fragment(), FolderAdapter.OnFolderClickListener, NoteRepository.OnDataChangeListener {
    private lateinit var foldersRecyclerView: RecyclerView
    private lateinit var foldersAdapter: FolderAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        foldersRecyclerView = view.findViewById(R.id.folders_recycler_view)
        foldersAdapter = FolderAdapter(NoteRepository.getAllFolders(), this)
        setupRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDataChanged() {
        loadFolders()
    }

    override fun onResume() {
        super.onResume()
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Folders"
        loadFolders()
    }

    private fun setupRecyclerView() {
        foldersRecyclerView.adapter = foldersAdapter
        foldersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadFolders() {
        val folders = NoteRepository.getAllFolders()
        foldersAdapter.setData(folders)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_folder_list, container, false)
    }

    override fun onFolderClick(folder: Folder) {
        val fragment = FolderNotesFragment.newInstance(folder.name)
        parentFragmentManager.beginTransaction()
            .replace(R.id.my_fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
