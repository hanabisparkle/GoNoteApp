package com.example.gonoteapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import com.example.gonoteapp.model.Note
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteFullViewFragment : Fragment() {
    private var currentNoteId: Long = -1L
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate your detail view layout
        return inflater.inflate(R.layout.fragment_note_full_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find the views in the detail layout
        val titleView: TextView = view.findViewById(R.id.note_full_title)
        val contentView: TextView = view.findViewById(R.id.note_full_content)
        val timestampView: TextView = view.findViewById(R.id.note_full_timestamp)

        val backButton: Button = view.findViewById(R.id.backbutton)
        val editButton: Button = view.findViewById(R.id.editbutton)

        // Get the ID from the arguments
        currentNoteId = arguments?.getLong("NOTE_ID") ?: -1L

        Log.d("NoteFullViewFragment", "Note ID: $currentNoteId")
        // Fetch the note from the repository using the ID
        val note = NoteRepository.getNoteById(currentNoteId)

        if (note != null) {
            titleView.text = note.title
            contentView.text = note.content
            timestampView.text = formatDate(note.timestamp)
        }

        // Retrieve the data from the fragment's arguments
//        val title = arguments?.getString("NOTE_TITLE")
//        val content = arguments?.getString("NOTE_CONTENT")
//        val timestamp = arguments?.getLong("NOTE_TIMESTAMP")

        // Populate the views with the note data
//        titleView.text = title
//        contentView.text = content
//        if (timestamp != null && timestamp != 0L) {
//            timestampView.text = formatDate(timestamp)
//        }

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        editButton.setOnClickListener {
            Log.d("NoteFullViewFragment", "Edit button clicked")
            val fragment = NewNoteFragment()
            // Pass the ID to the edit fragment
            fragment.arguments = bundleOf("NOTE_ID_TO_EDIT" to currentNoteId)

            parentFragmentManager.beginTransaction()
                .replace(R.id.my_fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        // LISTENER FOR RECEIVING EDITTED TEXT FROM NEWNOTEFRAGMENT
//        setFragmentResultListener("note_edited_request") { requestKey, bundle ->
//            // We receive the data here
//            val newTitle = bundle.getString("edited_title_key") ?: "Untitled"
//            val newContent = bundle.getString("edited_content_key") ?: ""
//
//            titleView.text = newTitle
//            contentView.text = newContent
//        }
    }

    private fun formatDate(millis: Long): String {
        // Use a more descriptive format for the full view
        val formatter = SimpleDateFormat("MMMM dd, yyyy 'at' h:mm a", Locale.getDefault())
        return formatter.format(Date(millis))
    }
}
