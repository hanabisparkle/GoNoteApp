package com.example.gonoteapp

import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gonoteapp.model.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ViewHolder untuk menampilkan satu item pratinjau catatan dalam RecyclerView.
 * Mengelola view untuk setiap item dan menangani interaksi pengguna.
 */
class NotePreviewHolder(itemView: View, private val listener: OnNoteClickListener) : RecyclerView.ViewHolder(itemView) {

    // Mendefinisikan view yang ada di dalam layout item catatan (note_preview.xml)
    private val title: TextView = itemView.findViewById(R.id.item_note_title)
    private val content: TextView = itemView.findViewById(R.id.item_note_content)
    private val timestamp: TextView = itemView.findViewById(R.id.item_note_timestamp)

    private val foldername: TextView = itemView.findViewById(R.id.item_note_folder)
    private val checkbox: CheckBox = itemView.findViewById(R.id.note_checkbox)

    /**
     * Mengikat data catatan ke view yang sesuai.
     * Fungsi ini dipanggil oleh [NotePreviewAdapter] untuk setiap item.
     *
     * @param note Data catatan yang akan ditampilkan.
     * @param isSelectionMode Status apakah mode seleksi sedang aktif.
     * @param isSelected Status apakah item ini sedang diseleksi.
     */
    fun bindNoteData(note: Note, isSelectionMode: Boolean, isSelected: Boolean, folderName: String) {
        title.text = note.title
        content.text = note.content
        timestamp.text = formatDate(note.timestamp)
        foldername.text = folderName

        // Tampilkan atau sembunyikan checkbox berdasarkan mode seleksi
        if (isSelectionMode) {
            checkbox.visibility = View.VISIBLE
            checkbox.isChecked = isSelected
        } else {
            checkbox.visibility = View.GONE
            checkbox.isChecked = false // Reset status checkbox
        }

        // Menangani aksi klik pada seluruh item view
        itemView.setOnClickListener {
            if (isSelectionMode) {
                // Jika mode seleksi, klik akan mengubah status checkbox
                checkbox.isChecked = !checkbox.isChecked
                // BARIS YANG HILANG: Memberi tahu listener bahwa status seleksi telah berubah
                listener.onNoteSelected(note, checkbox.isChecked)
            } else {
                // Jika tidak, klik akan membuka detail catatan
                listener.onNoteClicked(note)
            }
        }

        // Listener ini menangani klik LANGSUNG pada checkbox.
        // onClickListener di atas menangani klik pada seluruh baris.
        checkbox.setOnCheckedChangeListener { _, isChecked ->
             // Memberi tahu fragment bahwa status seleksi item ini berubah
            listener.onNoteSelected(note, isChecked)
        }
    }

    /**
     * Helper function untuk memformat timestamp (milidetik) menjadi string tanggal.
     * Contoh: "Oct 23"
     */
    private fun formatDate(millis: Long): String {
        val formatter = SimpleDateFormat("MMM dd", Locale.getDefault())
        return formatter.format(Date(millis))
    }
}
