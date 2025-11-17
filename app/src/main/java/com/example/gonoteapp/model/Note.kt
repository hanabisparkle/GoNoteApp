package com.example.gonoteapp.model

/**
 * Data class yang merepresentasikan satu objek Catatan (Note).
 * Ini adalah model data utama untuk aplikasi.
 */
data class Note(
    /**
     * ID unik untuk setiap catatan.
     * Digunakan sebagai pengenal utama di [com.example.gonoteapp.NoteRepository].
     */
    var id: Long,

    /**
     * ID dari folder tempat catatan ini berada.
     * Jika nilainya 0L, berarti catatan ini tidak masuk dalam folder manapun (Uncategorized).
     */
    var folderId: Long = 0L,

    /**
     * Judul catatan.
     */
    var title: String,

    /**
     * Isi atau konten dari catatan. Dapat mengandung format Markdown.
     */
    var content: String,

    /**
     * Timestamp (dalam milidetik) kapan catatan ini terakhir diubah.
     * Digunakan untuk mengurutkan catatan dari yang terbaru.
     */
    var timestamp: Long
)
