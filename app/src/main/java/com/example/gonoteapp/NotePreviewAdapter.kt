package com.example.gonoteapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gonoteapp.model.Note

class NotePreviewAdapter : RecyclerView.Adapter<NotePreviewHolder>() {

    private val notes = mutableListOf<Note>()

    fun setData(newNotes: List<Note>) {
        notes.clear()
        notes.addAll(newNotes)
        notifyDataSetChanged() // Tell RecyclerView to redraw
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotePreviewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.note_preview, parent, false)
        return NotePreviewHolder(view)
    }

    override fun onBindViewHolder(holder: NotePreviewHolder, position: Int) {
        holder.bindData(notes[position])
    }

    override fun getItemCount(): Int {
        return notes.size
    }
}
