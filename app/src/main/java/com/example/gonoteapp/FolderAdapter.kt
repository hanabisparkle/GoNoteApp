package com.example.gonoteapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gonoteapp.model.Folder

/**
 * Interface untuk menangani aksi klik pada folder.
 * Dipanggil oleh [FolderAdapter] dan diimplementasikan oleh fragment yang menampilkannya.
 */
interface OnFolderClickListener {
    /**
     * Dipanggil saat sebuah folder di-klik (bukan dalam mode seleksi).
     */
    fun onFolderClicked(folder: Folder)

    /**
     * Dipanggil saat status seleksi sebuah folder berubah.
     */
    fun onFolderSelected(folder: Folder, isSelected: Boolean)
}

/**
 * Adapter untuk menampilkan daftar folder dalam RecyclerView.
 * Mengelola data folder, mode seleksi, dan interaksi pengguna.
 */
class FolderAdapter(
    private val listener: OnFolderClickListener // Listener untuk event klik
) : RecyclerView.Adapter<FolderViewHolder>() {

    private val folders = mutableListOf<Folder>() // Daftar semua folder
    private val selectedFolders = mutableSetOf<Folder>() // Daftar folder yang diseleksi
    private var selectionMode = false // Status mode seleksi

    /**
     * Mengaktifkan atau menonaktifkan mode seleksi.
     * Dipanggil dari fragment untuk memulai atau mengakhiri mode seleksi.
     */
    fun setSelectionMode(isActive: Boolean) {
        selectionMode = isActive
        if (!isActive) {
            selectedFolders.clear() // Hapus semua seleksi saat mode seleksi berakhir
        }
        notifyDataSetChanged() // Gambar ulang daftar untuk menampilkan/menyembunyikan checkbox
    }

    /**
     * Mengembalikan daftar folder yang sedang diseleksi.
     * Dipanggil oleh fragment untuk mengetahui item mana yang akan dihapus atau dipindahkan.
     */
    fun getSelectedFolders(): Set<Folder> {
        return selectedFolders
    }

    /**
     * Memperbarui data folder yang akan ditampilkan.
     * Dipanggil oleh fragment saat data dari database sudah siap.
     */
    fun setData(newFolders: List<Folder>) {
        folders.clear()
        folders.addAll(newFolders)
        notifyDataSetChanged()
    }

    /**
     * Menambahkan folder ke dalam daftar seleksi.
     * Biasanya dipanggil saat proses restore state.
     */
    fun addSelected(folder: Folder) {
        selectedFolders.add(folder)
    }

    /**
     * Menghapus folder dari daftar seleksi.
     * Biasanya dipanggil saat proses restore state.
     */
    fun removeSelected(folder: Folder) {
        selectedFolders.remove(folder)
    }

    /**
     * Memilih semua folder yang ada di dalam adapter.
     * Dipanggil saat pengguna menekan tombol "Select All".
     */
    fun selectAll() {
        selectedFolders.clear()
        selectedFolders.addAll(folders)
        notifyDataSetChanged()
    }

    /**
     * Menghapus semua seleksi folder.
     * Dipanggil saat pengguna membatalkan seleksi semua.
     */
    fun deselectAll() {
        selectedFolders.clear()
        notifyDataSetChanged()
    }

    /**
     * Membuat ViewHolder baru untuk setiap item dalam daftar.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.folder_list_item, parent, false)
        return FolderViewHolder(view, listener) // Mengirim listener ke ViewHolder
    }

    /**
     * Menghubungkan data folder pada posisi tertentu dengan ViewHolder.
     */
    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = folders[position]
        val isSelected = selectedFolders.contains(folder)
        // Mengirim data dan status seleksi ke ViewHolder untuk ditampilkan
        holder.bindFolderData(folder, selectionMode, isSelected)
    }

    /**
     * Mengembalikan jumlah total item dalam daftar.
     */
    override fun getItemCount(): Int {
        return folders.size
    }

    /**
     * Mengambil data folder pada posisi tertentu.
     * Berguna untuk swipe-to-delete.
     */
    fun getFolderAt(position: Int): Folder {
        return folders[position]
    }
}
