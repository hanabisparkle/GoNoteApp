package com.example.gonoteapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gonoteapp.model.Note

interface OnNoteClickListener {
    fun onNoteClicked(note: Note)
}

class NotePreviewAdapter(
    private val listener: OnNoteClickListener
) : RecyclerView.Adapter<NotePreviewHolder>() {

    private val notes = mutableListOf<Note>()
    private var isSelectionMode = false

    fun setSelectionMode(isActive: Boolean) {
        isSelectionMode = isActive
        notifyDataSetChanged()
    }

    fun setData(newNotes: List<Note>) {
        notes.clear()
        notes.addAll(newNotes)
        notifyDataSetChanged()
    }
    
    fun getNoteAt(position: Int): Note {
        return notes[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotePreviewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.note_preview, parent, false)
        return NotePreviewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: NotePreviewHolder, position: Int) {
        holder.bindData(notes[position], isSelectionMode)
    }

    override fun getItemCount(): Int {
        return notes.size
    }
}
