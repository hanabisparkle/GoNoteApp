package com.example.gonoteapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainFragment : BaseNoteListFragment() {

    private var isSelectionMode = false
    private lateinit var deleteButton: Button
    private lateinit var addToFolderButton: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val createButton: FloatingActionButton = view.findViewById(R.id.createbutton)
        createButton.setOnClickListener {
            showCreateDialog()
        }

        val selectButton: Button = view.findViewById(R.id.select_button)
        deleteButton = view.findViewById(R.id.delete_button)
        addToFolderButton = view.findViewById(R.id.add_to_folder_button)

        selectButton.setOnClickListener {
            isSelectionMode = !isSelectionMode
            noteAdapter.setSelectionMode(isSelectionMode)

            if (isSelectionMode) {
                deleteButton.visibility = View.VISIBLE
                addToFolderButton.visibility = View.VISIBLE
            } else {
                deleteButton.visibility = View.GONE
                addToFolderButton.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Home"
    }

    override fun loadNotes() {
        noteAdapter.setData(NoteRepository.getAllNotes())
    }

    private fun showCreateDialog() {
        val options = arrayOf("New Note", "New Folder")
        AlertDialog.Builder(requireContext())
            .setTitle("Create")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.my_fragment_container, NewNoteFragment())
                            .addToBackStack(null)
                            .commit()
                    }
                    1 -> {
                        showNewFolderDialog()
                    }
                }
            }
            .show()
    }

    private fun showNewFolderDialog() {
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
