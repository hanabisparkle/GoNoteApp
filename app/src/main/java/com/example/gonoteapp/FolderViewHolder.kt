package com.example.gonoteapp

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gonoteapp.model.Folder

/**
 * ViewHolder untuk menampilkan satu item folder dalam RecyclerView.
 * Mengelola view untuk setiap item dan menangani interaksi pengguna.
 */
class FolderViewHolder(itemView: View, private val listener: OnFolderClickListener) : RecyclerView.ViewHolder(itemView) {

    // Mendefinisikan view yang ada di dalam layout item folder (folder_list_item.xml)
    private val folderName: TextView = itemView.findViewById(R.id.folder_name_textview)
    private val checkbox: CheckBox = itemView.findViewById(R.id.folder_checkbox)

    /**
     * Mengikat data folder ke view yang sesuai.
     * Fungsi ini dipanggil oleh [FolderAdapter] untuk setiap item yang terlihat di layar.
     *
     * @param folder Data folder yang akan ditampilkan.
     * @param isSelectionMode Status apakah mode seleksi sedang aktif.
     * @param isSelected Status apakah item ini sedang diseleksi.
     */
    fun bindFolderData(folder: Folder, isSelectionMode: Boolean, isSelected: Boolean) {
        folderName.text = folder.name
        checkbox.isChecked = isSelected

        // Tampilkan atau sembunyikan checkbox berdasarkan mode seleksi
        if (isSelectionMode) {
            checkbox.visibility = View.VISIBLE
        } else {
            checkbox.visibility = View.GONE
            checkbox.isChecked = false // Reset status checkbox saat mode seleksi tidak aktif
        }

        // Menangani aksi klik pada seluruh item view
        itemView.setOnClickListener {
            if (isSelectionMode) {
                // Jika dalam mode seleksi, klik pada item akan mengubah status checkbox
                checkbox.isChecked = !checkbox.isChecked
            } else {
                // Jika tidak, klik akan membuka folder (memanggil listener)
                listener.onFolderClicked(folder)
            }
        }

        // Listener untuk memantau perubahan status checkbox
        checkbox.setOnCheckedChangeListener { _, isChecked ->
            // Memberi tahu fragment (melalui adapter) bahwa status seleksi item ini berubah
            listener.onFolderSelected(folder, isChecked)
        }
    }
}
