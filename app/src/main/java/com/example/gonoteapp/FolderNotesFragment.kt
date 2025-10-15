
package com.example.gonoteapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class FolderNotesFragment : BaseNoteListFragment() {

    private lateinit var folderName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            folderName = it.getString(ARG_FOLDER_NAME) ?: ""
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.title = folderName
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        // When we resume this fragment, ensure the title is set correctly.
        (activity as? AppCompatActivity)?.supportActionBar?.title = folderName
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        // When leaving the fragment, reset the title to "Folders"
        activity.supportActionBar?.title = "Folders"
    }

    override fun loadNotes() {
        // The implementation for this screen is to load notes for a specific folder
        noteAdapter.setData(NoteRepository.getNotesForFolder(folderName))
    }

    companion object {
        private const val ARG_FOLDER_NAME = "folder_name"

        fun newInstance(folderName: String): FolderNotesFragment {
            val fragment = FolderNotesFragment()
            val args = Bundle()
            args.putString(ARG_FOLDER_NAME, folderName)
            fragment.arguments = args
            return fragment
        }
    }
}
