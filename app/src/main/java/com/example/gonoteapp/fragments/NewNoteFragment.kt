package com.example.gonoteapp.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.gonoteapp.NewNoteViewModel
import com.example.gonoteapp.R
import io.noties.markwon.Markwon
import io.noties.markwon.editor.MarkwonEditor
import io.noties.markwon.editor.MarkwonEditorTextWatcher
import java.util.concurrent.Executors

class NewNoteFragment : Fragment() {

    private val viewModel: NewNoteViewModel by viewModels()

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            appendOcrDummyText()
        }
    }

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
        val cancelButton: ImageButton = view.findViewById(R.id.cancelbutton)
        val saveButton: ImageButton = view.findViewById(R.id.save_button)
        val scanButton: ImageButton = view.findViewById(R.id.scan_button)

        val folderName = arguments?.getString("FOLDER_NAME")

        // --- MARKWON IMPLEMENTATION START ---

        val markwon = Markwon.create(requireContext())
        val editor = MarkwonEditor.create(markwon)
        contentEditText.addTextChangedListener(
            MarkwonEditorTextWatcher.withProcess(editor)
        )

        // --- MARKWON IMPLEMENTATION END ---

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

        scanButton.setOnClickListener {
            showImageSourceDialog()
        }

        saveButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val content = contentEditText.text.toString()
            viewModel.saveNote(title, content, folderName)
        }

        cancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Camera", "Gallery")
        AlertDialog.Builder(requireContext())
            .setTitle("Choose Image Source")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> { // Camera
                        appendOcrDummyText()
                    }
                    1 -> { // Gallery
                        selectImageLauncher.launch("image/*")
                    }
                }
            }
            .show()
    }

    private fun appendOcrDummyText() {
        val contentEditText: EditText = view?.findViewById(R.id.new_note_content) ?: return
        val currentText = contentEditText.text.toString()
        val newText = if (currentText.isEmpty()) {
            "[Scanned OCR text will appear here]"
        } else {
            "$currentText [Scanned OCR text will appear here]"
        }
        contentEditText.setText(newText)
        contentEditText.setSelection(newText.length)
    }
}
