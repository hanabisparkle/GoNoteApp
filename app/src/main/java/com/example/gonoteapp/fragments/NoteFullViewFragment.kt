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
import androidx.fragment.app.setFragmentResultListener
import com.example.gonoteapp.NoteRepository
import com.example.gonoteapp.R
import io.noties.markwon.Markwon
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteFullViewFragment : Fragment(), NoteRepository.OnDataChangeListener {

    private var currentNoteId: Long = -1L
    private lateinit var titleView: TextView
    private lateinit var contentView: TextView
    private lateinit var timestampView: TextView
    private lateinit var markwon: Markwon

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Listener untuk ketika kembali ke halaman ini setelah mengedit note
        setFragmentResultListener("note_edited_request") { _, bundle ->
            val noteId = bundle.getLong("edited_id_key")
            val newTitle = bundle.getString("edited_title_key") ?: "Untitled"
            val newContent = bundle.getString("edited_content_key") ?: ""
            NoteRepository.updateNote(noteId, newTitle, newContent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_note_full_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        titleView = view.findViewById(R.id.note_full_title)
        contentView = view.findViewById(R.id.note_full_content)
        contentView.movementMethod = ScrollingMovementMethod.getInstance()
        timestampView = view.findViewById(R.id.note_full_timestamp)

        markwon = Markwon.create(requireContext()) // markwon sebagai library yang digunakan
        // sehingga isi note dapat menggunakan format markdown (contoh: **bold**, *italic*)

        val backButton: ImageButton = view.findViewById(R.id.backbutton)
        val editButton: ImageButton = view.findViewById(R.id.editbutton)
        currentNoteId = arguments?.getLong("NOTE_ID") ?: -1L
        updateNoteData()

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        editButton.setOnClickListener {
            val fragment = NewNoteFragment()
            fragment.arguments = bundleOf("NOTE_ID_TO_EDIT" to currentNoteId)

            parentFragmentManager.beginTransaction()
                .replace(R.id.my_fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDataChanged() {
        updateNoteData()
    }

    private fun updateNoteData() {
        val note = NoteRepository.getNoteById(currentNoteId)

        if (note != null && view != null) {
            titleView.text = note.title
            markwon.setMarkdown(contentView, note.content)
            timestampView.text = formatDate(note.timestamp)
        }
    }

    private fun formatDate(millis: Long): String {
        val formatter = SimpleDateFormat("MMMM dd, yyyy 'at' h:mm a", Locale.getDefault())
        return formatter.format(Date(millis))
    }
}
