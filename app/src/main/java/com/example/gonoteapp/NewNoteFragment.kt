package com.example.gonoteapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import com.example.gonoteapp.model.Note

// Make sure your class name is NewNoteFragment
// ... (imports)
class NewNoteFragment : Fragment() {

    private var noteIdToEdit: Long = -1L
    private var isEditMode = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleEditText: EditText = view.findViewById(R.id.new_note_title)
        val contentEditText: EditText = view.findViewById(R.id.new_note_content)
        val cancelButton: Button = view.findViewById(R.id.cancelbutton)
        val saveButton: Button = view.findViewById(R.id.save_button)

        // Check if an ID was passed for editing
        noteIdToEdit = arguments?.getLong("NOTE_ID_TO_EDIT", -1L) ?: -1L
        Log.d("NewNoteFragment", "Note ID to Edit: $noteIdToEdit")
        isEditMode = noteIdToEdit != -1L

        if (isEditMode) {
            // In Edit Mode: Load the existing note data
            val note = NoteRepository.getNoteById(noteIdToEdit)
            if (note != null) {
                titleEditText.setText(note.title)
                contentEditText.setText(note.content)
            }
        }

        saveButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val content = contentEditText.text.toString()

            if (isEditMode) {
                // UPDATE: Send a result back for the edited note
                setFragmentResult("note_edited_request", bundleOf(
                    "edited_id_key" to noteIdToEdit,
                    "edited_title_key" to title,
                    "edited_content_key" to content
                ))
            } else {
                // CREATE: Send a result back for the new note
                setFragmentResult("new_note_request", bundleOf(
                    "note_title_key" to title,
                    "note_content_key" to content
                ))
            }
            parentFragmentManager.popBackStack()
        }

        cancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}
