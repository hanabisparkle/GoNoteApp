package com.example.gonoteapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gonoteapp.model.Note

/**
 * Interface untuk menangani aksi klik pada item catatan.
 * Dipanggil oleh [NotePreviewAdapter] dan diimplementasikan oleh fragment yang menampilkannya.
 */
interface OnNoteClickListener {
    /**
     * Dipanggil saat sebuah catatan di-klik (bukan dalam mode seleksi).
     */
    fun onNoteClicked(note: Note)

    /**
     * Dipanggil saat status seleksi sebuah catatan berubah.
     */
    fun onNoteSelected(note: Note, isSelected: Boolean)
}

/**
 * Adapter untuk menampilkan daftar pratinjau catatan dalam RecyclerView.
 * Mirip dengan [FolderAdapter], tetapi untuk objek [Note].
 */
class NotePreviewAdapter(
    private val listener: OnNoteClickListener // Listener untuk event klik
) : RecyclerView.Adapter<NotePreviewHolder>() {

    private val notes = mutableListOf<Note>() // Daftar semua catatan
    private val selectedNotes = mutableSetOf<Note>() // Daftar catatan yang diseleksi
    private var selectionMode = false // Status mode seleksi

    /**
     * Mengaktifkan atau menonaktifkan mode seleksi.
     */
    fun setSelectionMode(isActive: Boolean) {
        selectionMode = isActive
        notifyDataSetChanged() // Gambar ulang daftar
    }

    /**
     * Memperbarui data catatan yang akan ditampilkan.
     */
    fun setData(newNotes: List<Note>) {
        notes.clear()
        notes.addAll(newNotes)
        notifyDataSetChanged()
    }

    /**
     * Mengambil data catatan pada posisi tertentu.
     * Berguna untuk swipe-to-delete.
     */
    fun getNoteAt(position: Int): Note {
        return notes[position]
    }

    /**
     * Mengembalikan daftar catatan yang sedang diseleksi.
     */
    fun getSelectedNotes(): Set<Note> {
        return selectedNotes
    }

    /**
     * Memilih semua catatan.
     */
    fun selectAll() {
        selectedNotes.clear()
        selectedNotes.addAll(notes)
        notifyDataSetChanged()
    }

    /**
     * Menghapus semua seleksi.
     */
    fun deselectAll() {
        selectedNotes.clear()
        notifyDataSetChanged()
    }

    /**
     * Menambahkan catatan ke dalam daftar seleksi.
     */
    fun addSelected(note: Note) {
        selectedNotes.add(note)
    }

    /**
     * Menghapus catatan dari daftar seleksi.
     */
    fun removeSelected(note: Note) {
        selectedNotes.remove(note)
    }

    /**
     * Membuat ViewHolder baru untuk setiap item catatan.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotePreviewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.note_preview, parent, false)
        return NotePreviewHolder(view, listener) // Mengirim listener ke ViewHolder
    }

    /**
     * Menghubungkan data catatan pada posisi tertentu dengan ViewHolder.
     */
    override fun onBindViewHolder(holder: NotePreviewHolder, position: Int) {
        val note = notes[position]
        val isSelected = selectedNotes.contains(note)
        // Mengirim data dan status seleksi ke ViewHolder
        holder.bindNoteData(note, selectionMode, isSelected)
    }

    /**
     * Mengembalikan jumlah total item dalam daftar.
     */
    override fun getItemCount(): Int {
        return notes.size
    }
}
