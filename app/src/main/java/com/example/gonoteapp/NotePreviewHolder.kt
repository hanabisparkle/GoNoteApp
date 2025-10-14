package com.example.gonoteapp

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gonoteapp.model.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotePreviewHolder(itemView: View, private val listener: OnNoteClickListener) : RecyclerView.ViewHolder(itemView) {

    private val title: TextView = itemView.findViewById(R.id.item_note_title)
    private val content: TextView = itemView.findViewById(R.id.item_note_content)
    private val timestamp: TextView = itemView.findViewById(R.id.item_note_timestamp)

    private var currentNote: Note? = null

    fun bindData(note: Note) {
        title.text = note.title
        content.text = note.content
        timestamp.text = formatDate(note.timestamp)

        // Set the click listener here!
        itemView.setOnClickListener {
            listener.onNoteClicked(note) // Now it knows which note to pass
        }
    }

    private fun formatDate(millis: Long): String {
        val formatter = SimpleDateFormat("MMM dd", Locale.getDefault())
        return formatter.format(Date(millis))
    }
}
