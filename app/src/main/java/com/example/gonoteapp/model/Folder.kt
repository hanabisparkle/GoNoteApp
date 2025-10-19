package com.example.gonoteapp.model

/**
 * Data class yang merepresentasikan satu objek Folder.
 * Digunakan di seluruh aplikasi untuk menyimpan informasi tentang folder.
 */
data class Folder(
    /**
     * ID unik untuk setiap folder.
     * Digunakan sebagai pengenal utama di [NoteRepository].
     */
    val id: Long,

    /**
     * Nama folder yang akan ditampilkan kepada pengguna.
     */
    var name: String,

    /**
     * Jumlah catatan yang ada di dalam folder ini.
     * Dihitung secara dinamis oleh [NoteRepository.getAllFolders].
     */
    var noteCount: Int = 0
)
