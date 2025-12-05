package com.example.gonoteapp

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.gonoteapp.fragments.FolderNotesFragment
import com.example.gonoteapp.model.Folder
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

    fun onNoteLongHold(note: Note)

    /**
     * Dipanggil saat status seleksi sebuah catatan berubah.
     */
    fun onNoteSelected(note: Note, isSelected: Boolean)

    fun onGoToFolderClicked(note: Note, folder: Folder)

    //fun onDeleteFolderClicked(note: Note)

    //fun onSelectNoteClicked(note: Note)
}

/**
 * Adapter untuk menampilkan daftar pratinjau catatan dalam RecyclerView.
 * Mirip dengan [FolderAdapter], tetapi untuk objek [Note].
 */
class NotePreviewAdapter(
    private val listener: OnNoteClickListener,// Listener untuk event klik
    private val repository: NoteRepository
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
        var noteFolderName = "No Folder"

        if (note.folderId != 0L) {
            val noteFolder = repository.getFolderById(note.folderId)
            noteFolderName = noteFolder?.name ?:"No Folder"
        }

        holder.itemView.setOnLongClickListener {
            listener.onNoteLongHold(note)
            true
        }

        // Mengirim data dan status seleksi ke ViewHolder
        holder.bindNoteData(note, selectionMode, isSelected, noteFolderName)
    }

    /**
     * Mengembalikan jumlah total item dalam daftar.
     */
    override fun getItemCount(): Int {
        return notes.size
    }

    fun showLongHoldMenu(note: Note, anchor: View) {
        val popup = PopupMenu(anchor.context, anchor)
        popup.menuInflater.inflate(R.menu.note_hold_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when(item.itemId) {
                R.id.menu_go_to_folder -> {
                    // mencari folder dan menugaskan aksi ke fragment BaseNoteList
                    repository.getFolderById(note.folderId)?.let {
                            folder ->
                        listener.onGoToFolderClicked(note, folder)
                    }
                    true
                }
                R.id.menu_delete_note -> {
                    //deleteNote(note.id)
                    true
                }
                R.id.menu_select_note -> {
                    //deleteNote(note.id)
                    true
                }
                else -> false
            }
        }
        popup.gravity = Gravity.END
        popup.show()
    }


    /**
     * Finds the current position (index) of a given note in the adapter's list.
     * This is the REVERSE of getNoteAt().
     *
     * @param note The note object to find.
     * @return The index of the note in the list, or -1 if not found.
     */
    fun getNotePosition(note: Note): Int {
        // Use the indexOf function on the list to find the note's position.
        return notes.indexOf(note)
    }

}
