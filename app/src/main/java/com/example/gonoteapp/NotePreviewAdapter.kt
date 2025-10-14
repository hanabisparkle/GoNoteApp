package com.example.gonoteapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gonoteapp.model.Note

// 1. Define the interface. This can be inside or outside the adapter class.
//    Placing it here keeps related code together.
interface OnNoteClickListener {
    fun onNoteClicked(note: Note)
}

// 2. Modify the adapter's constructor to require a listener.
class NotePreviewAdapter(
    private val listener: OnNoteClickListener
) : RecyclerView.Adapter<NotePreviewHolder>() {

    private val notes = mutableListOf<Note>()

    fun setData(newNotes: List<Note>) {
        notes.clear()
        notes.addAll(newNotes)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotePreviewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.note_preview, parent, false)
        // 3. Pass the listener along to the ViewHolder when it's created.
        return NotePreviewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: NotePreviewHolder, position: Int) {
        holder.bindData(notes[position])
    }

    override fun getItemCount(): Int {
        return notes.size
    }
}
