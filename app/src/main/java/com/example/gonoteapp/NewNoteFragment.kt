package com.example.gonoteapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

class NewNoteFragment : Fragment() {

    private val viewModel: NewNoteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleEditText: EditText = view.findViewById(R.id.new_note_title)
        val contentEditText: EditText = view.findViewById(R.id.new_note_content)
        val cancelButton: Button = view.findViewById(R.id.cancelbutton)
        val saveButton: Button = view.findViewById(R.id.save_button)

        val noteIdToEdit = arguments?.getLong("NOTE_ID_TO_EDIT", -1L) ?: -1L
        viewModel.loadNote(noteIdToEdit)


        viewModel.note.observe(viewLifecycleOwner) { note ->
            if (note != null) {
                titleEditText.setText(note.title)
                contentEditText.setText(note.content)
            }
        }

        viewModel.navigateBack.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                parentFragmentManager.popBackStack()
                viewModel.onNavigateBackComplete()
            }
        }


        saveButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val content = contentEditText.text.toString()
            viewModel.saveNote(title, content)
        }

        cancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}
