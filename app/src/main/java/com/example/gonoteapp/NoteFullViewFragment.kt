package com.example.gonoteapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteFullViewFragment : Fragment() {

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

        // Retrieve the data from the fragment's arguments
        val title = arguments?.getString("NOTE_TITLE")
        val content = arguments?.getString("NOTE_CONTENT")
        val timestamp = arguments?.getLong("NOTE_TIMESTAMP")

        // Populate the views with the note data
        titleView.text = title
        contentView.text = content
        if (timestamp != null && timestamp != 0L) {
            timestampView.text = formatDate(timestamp)
        }

        backButton.setOnClickListener {
            // 3. Use parentFragmentManager to go back
            // This is the same action as pressing the device's back button.
            parentFragmentManager.popBackStack()
        }

        // Handle the toolbar's back button click
    }

    private fun formatDate(millis: Long): String {
        // Use a more descriptive format for the full view
        val formatter = SimpleDateFormat("MMMM dd, yyyy 'at' h:mm a", Locale.getDefault())
        return formatter.format(Date(millis))
    }
}
