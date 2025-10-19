package com.example.gonoteapp.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.gonoteapp.NewNoteViewModel
import com.example.gonoteapp.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.noties.markwon.Markwon
import io.noties.markwon.editor.MarkwonEditor
import io.noties.markwon.editor.MarkwonEditorTextWatcher

/**
 * Fragment untuk membuat catatan baru atau mengedit catatan yang sudah ada.
 * Menggunakan [NewNoteViewModel] untuk memisahkan logika UI dan data.
 */
class NewNoteFragment : Fragment() {

    // Inisialisasi ViewModel menggunakan delegasi 'by viewModels()'.
    private val viewModel: NewNoteViewModel by viewModels()

    // Launcher untuk memilih gambar dari galeri.
    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Saat ini hanya menambahkan teks dummy. Nantinya akan diintegrasikan dengan model ML.
            appendOcrDummyText()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi semua view dari layout.
        val titleEditText: EditText = view.findViewById(R.id.new_note_title)
        val contentEditText: EditText = view.findViewById(R.id.new_note_content)
        val cancelButton: ImageButton = view.findViewById(R.id.cancelbutton)
        val saveButton: ImageButton = view.findViewById(R.id.save_button)
        val scanButton: ImageButton = view.findViewById(R.id.scan_button)

        // Mengambil nama folder jika catatan ini dibuat dari dalam sebuah folder.
        val folderName = arguments?.getString("FOLDER_NAME")

        // Konfigurasi Markwon untuk editor teks markdown.
        val markwon = Markwon.create(requireContext())
        val editor = MarkwonEditor.create(markwon)
        contentEditText.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(editor))

        // Periksa apakah ini mode edit dengan mengambil ID catatan dari arguments.
        val noteIdToEdit = arguments?.getLong("NOTE_ID_TO_EDIT", -1L) ?: -1L
        viewModel.loadNote(noteIdToEdit)

        // Mengobservasi LiveData dari ViewModel.
        // Jika ada data catatan, tampilkan di EditText.
        viewModel.note.observe(viewLifecycleOwner) { note ->
            if (note != null && viewModel.isEditMode) {
                titleEditText.setText(note.title)
                contentEditText.setText(note.content)
            }
        }

        // Mengobservasi sinyal untuk navigasi kembali.
        viewModel.navigateBack.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                parentFragmentManager.popBackStack() // Kembali ke layar sebelumnya
                viewModel.onNavigateBackComplete() // Reset sinyal
            }
        }

        // Listener untuk tombol scan (OCR).
        scanButton.setOnClickListener {
            showImageSourceDialog()
        }

        // Listener untuk tombol simpan.
        saveButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val content = contentEditText.text.toString()
            // Panggil ViewModel untuk menyimpan catatan.
            viewModel.saveNote(title, content, folderName)
        }

        // Listener untuk tombol batal.
        cancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    /**
     * Menampilkan dialog untuk memilih sumber gambar (Kamera atau Galeri).
     */
    private fun showImageSourceDialog() {
        val options = arrayOf("Camera", "Gallery")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Choose Image Source")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> { // Pilihan "Camera"
                        appendOcrDummyText() // Placeholder
                    }
                    1 -> { // Pilihan "Gallery"
                        selectImageLauncher.launch("image/*")
                    }
                }
            }
            .show()
    }

    /**
     * Menambahkan teks dummy sebagai placeholder untuk hasil OCR.
     */
    private fun appendOcrDummyText() {
        val contentEditText: EditText = view?.findViewById(R.id.new_note_content) ?: return
        val currentText = contentEditText.text.toString()
        val newText = if (currentText.isEmpty()) {
            "[Scanned OCR text will appear here]"
        } else {
            "$currentText\n[Scanned OCR text will appear here]"
        }
        contentEditText.setText(newText)
        contentEditText.setSelection(newText.length) // Pindahkan kursor ke akhir teks
    }
}
