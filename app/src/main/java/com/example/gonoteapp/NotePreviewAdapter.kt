package com.example.gonoteapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gonoteapp.model.Note

interface OnNoteClickListener {
    fun onNoteClicked(note: Note)
    fun onNoteSelected(note: Note, isSelected: Boolean)
}
class NotePreviewAdapter(
    private val listener: OnNoteClickListener
) : RecyclerView.Adapter<NotePreviewHolder>() {

    private val notes = mutableListOf<Note>()
    private val selectedNotes = mutableSetOf<Note>()
    private var selectionMode = false

    fun setSelectionMode(isActive: Boolean) {
        selectionMode = isActive
        if (!isActive) {
            selectedNotes.clear()
        }
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

    fun getSelectedNotes(): Set<Note> {
        return selectedNotes
    }

    fun selectAll() {
        selectedNotes.clear()
        selectedNotes.addAll(notes)
        notifyDataSetChanged()
    }

    fun deselectAll() {
        selectedNotes.clear()
        notifyDataSetChanged()
    }

    fun addSelected(note: Note) {
        selectedNotes.add(note)
    }

    fun removeSelected(note: Note) {
        selectedNotes.remove(note)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotePreviewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.note_preview, parent, false)
        return NotePreviewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: NotePreviewHolder, position: Int) {
        val note = notes[position]
        val isSelected = selectedNotes.contains(note)
        holder.bindNoteData(note, selectionMode, isSelected)
    }

    override fun getItemCount(): Int {
        return notes.size
    }
}
