package com.example.gonoteapp

import android.view.View
import android.widget.CheckBox
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
    private val checkbox: CheckBox = itemView.findViewById(R.id.note_checkbox)


    fun bindData(note: Note, isSelectionMode: Boolean) {
        title.text = note.title
        content.text = note.content
        timestamp.text = formatDate(note.timestamp)

        if (isSelectionMode) {
            checkbox.visibility = View.VISIBLE
        } else {
            checkbox.visibility = View.GONE
        }

        itemView.setOnClickListener {
            if (!isSelectionMode) {
                listener.onNoteClicked(note)
            }
        }
    }

    private fun formatDate(millis: Long): String {
        val formatter = SimpleDateFormat("MMM dd", Locale.getDefault())
        return formatter.format(Date(millis))
    }
}
