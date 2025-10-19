package com.example.gonoteapp.fragments

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.gonoteapp.NoteRepository
import com.example.gonoteapp.R
import io.noties.markwon.Markwon
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Fragment untuk menampilkan isi lengkap dari sebuah catatan.
 * Tampilan ini hanya untuk membaca (read-only), dengan opsi untuk mengedit.
 */
class NoteFullViewFragment : Fragment() {

    private var currentNoteId: Long = -1L
    private lateinit var titleView: TextView
    private lateinit var contentView: TextView
    private lateinit var timestampView: TextView
    private lateinit var markwon: Markwon // Library untuk merender Markdown

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_note_full_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi view
        titleView = view.findViewById(R.id.note_full_title)
        contentView = view.findViewById(R.id.note_full_content)
        contentView.movementMethod = ScrollingMovementMethod.getInstance() // Aktifkan scrolling pada konten
        timestampView = view.findViewById(R.id.note_full_timestamp)
        val backButton: ImageButton = view.findViewById(R.id.backbutton)
        val editButton: ImageButton = view.findViewById(R.id.editbutton)

        // Inisialisasi Markwon
        markwon = Markwon.create(requireContext())

        // Ambil ID catatan dari arguments
        currentNoteId = arguments?.getLong("NOTE_ID") ?: -1L
        updateNoteData() // Muat dan tampilkan data catatan

        // Listener untuk tombol kembali
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Listener untuk tombol edit
        editButton.setOnClickListener {
            val fragment = NewNoteFragment()
            // Kirim ID catatan ke NewNoteFragment untuk mode edit
            fragment.arguments = bundleOf("NOTE_ID_TO_EDIT" to currentNoteId)

            parentFragmentManager.beginTransaction()
                .replace(R.id.my_fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    /**
     * Memuat ulang data catatan setiap kali ada perubahan data di repository.
     * Ini berguna jika catatan diedit di tempat lain.
     */
    override fun onResume() {
        super.onResume()
        updateNoteData()
    }

    /**
     * Mengambil data catatan dari repository dan menampilkannya di view.
     */
    private fun updateNoteData() {
        val note = NoteRepository.getNoteById(currentNoteId)

        if (note != null && view != null) {
            titleView.text = note.title
            // Gunakan Markwon untuk menampilkan konten sebagai Markdown
            markwon.setMarkdown(contentView, note.content)
            timestampView.text = formatDate(note.timestamp)
        }
    }

    /**
     * Helper function untuk memformat timestamp menjadi format tanggal yang lebih lengkap.
     * Contoh: "October 23, 2024 at 10:30 AM"
     */
    private fun formatDate(millis: Long): String {
        val formatter = SimpleDateFormat("MMMM dd, yyyy 'at' h:mm a", Locale.getDefault())
        return formatter.format(Date(millis))
    }
}
