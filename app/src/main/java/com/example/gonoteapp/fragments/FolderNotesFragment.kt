package com.example.gonoteapp.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.gonoteapp.MainActivity
import com.example.gonoteapp.NoteRepository
import com.example.gonoteapp.NoteRepository.getFolderById
import kotlin.properties.Delegates

class FolderNotesFragment : BaseNoteListFragment() {

    private lateinit var folderName: String
    private var folderId by Delegates.notNull<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            folderId = it.getLong(ARG_FOLDER_ID)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        folderName = getFolderById(folderId)?.name ?: "Unknown Folder"
        (activity as? MainActivity)?.updateTitle(folderName)
        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        // When we resume this fragment, ensure the title is set correctly.
        (activity as? MainActivity)?.updateTitle(folderName)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun loadNotes() {
        // The implementation for this screen is to load notes for a specific folder
        noteAdapter.setData(NoteRepository.getNotesForFolder(folderId))
    }

    companion object {
        private const val ARG_FOLDER_ID = "folder_id"
        fun newInstance(folderId: Long): FolderNotesFragment {
            val fragment = FolderNotesFragment()
            val args = Bundle()
            args.putLong(ARG_FOLDER_ID, folderId)
            fragment.arguments = args
            return fragment
        }
    }
}