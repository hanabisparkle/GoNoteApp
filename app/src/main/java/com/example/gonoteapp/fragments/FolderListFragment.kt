package com.example.gonoteapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gonoteapp.FolderAdapter
import com.example.gonoteapp.NoteRepository
import com.example.gonoteapp.R
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

    fun showNewFolderDialog() {
        val editText = EditText(requireContext()).apply {
            hint = "Folder Name"
        }

        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 40, 60, 20)
            addView(editText)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("New Folder")
            .setView(layout)
            .setPositiveButton("Create") { _, _ ->
                val folderName = editText.text.toString()
                if (folderName.isNotBlank()) {
                    NoteRepository.addFolder(folderName)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
