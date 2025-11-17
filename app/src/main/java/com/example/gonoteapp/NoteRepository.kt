package com.example.gonoteapp

import android.util.Log
import com.example.gonoteapp.model.Folder
import com.example.gonoteapp.model.Note

/**
 * Repository tunggal (singleton) yang mengelola semua data catatan dan folder.
 * Bertindak sebagai sumber data utama (source of truth) untuk seluruh aplikasi.
 * PENTING: Saat ini, semua data disimpan di memori dan akan hilang saat aplikasi ditutup.
 * Ini adalah implementasi sederhana untuk tujuan demonstrasi.
 */
object NoteRepository {

    private const val NOTE_REPOSITORY = "NOTE_REPOSITORY"
    private val notes = mutableListOf<Note>() // Daftar internal untuk semua catatan
    private val folders = mutableListOf<Folder>() // Daftar internal untuk semua folder
    private var nextId = 1L // Untuk auto-increment ID catatan
    private var nextFolderId = 1L // Untuk auto-increment ID folder

    /**
     * Blok inisialisasi yang dijalankan saat repository pertama kali dibuat.
     * Digunakan untuk mengisi data dummy agar aplikasi tidak kosong saat pertama kali dijalankan.
     */
    init {
        val folder1 = Folder(nextFolderId++, "Personal")
        val folder2 = Folder(nextFolderId++, "Work")
        folders.addAll(listOf(folder1, folder2))

        val initialNotes = listOf(
            Note(nextId++, folder2.id,"Meeting Notes", "Discuss Q3 budget and project timelines.", System.currentTimeMillis()),
            Note(nextId++, folder1.id, "Shopping List", "Milk, Bread, Eggs, Coffee.", System.currentTimeMillis()),
            Note(nextId++, folder1.id, "Book Ideas", "A story about a time-traveling librarian.", System.currentTimeMillis()),
            Note(nextId++, 0L, "Workout Plan", "Monday: Chest, Tuesday: Back, Wednesday: Legs.", System.currentTimeMillis()) // 0L berarti tidak ada folder
        )
        notes.addAll(initialNotes)
    }

    // --- FUNGSI UNTUK FOLDER ---

    /**
     * Menambahkan folder baru.
     */
    fun addFolder(name: String) {
        folders.add(Folder(nextFolderId++, name))
        Log.d(NOTE_REPOSITORY, "addFolder() -> Folder Name: ${name}")
    }

    /**
     * Mengambil semua folder. Juga menghitung jumlah catatan di setiap folder.
     */
    fun getAllFolders(): List<Folder> {
        for (f in folders) {
            f.noteCount = notes.count { it.folderId == f.id }
        }
        return folders.toList()
    }

    /**
     * Mencari folder berdasarkan ID-nya.
     */
    fun getFolderById(id: Long): Folder? {
        return folders.find { it.id == id }
    }

    /**
     * Memperbarui nama folder yang sudah ada.
     */
    fun updateFolder(id: Long, newName: String) {
        val folderToUpdate = getFolderById(id)
        folderToUpdate?.name = newName
        Log.d(NOTE_REPOSITORY, "updateFolder() -> Folder has been updated")
    }

    /**
     * Menghapus folder berdasarkan ID.
     */
    fun deleteFolder(id: Long) {
        folders.removeAll { it.id == id }
        Log.d(NOTE_REPOSITORY, "deleteFolder() -> Folder has been deleted")
    }

    // --- FUNGSI UNTUK CATATAN ---

    /**
     * Menambahkan catatan baru. Bisa dengan atau tanpa folder.
     */
    fun addNote(title: String, content: String, folderName: String? = null) {
        val folderId = folderName?.let { name ->
            folders.find { it.name == name }?.id
        } ?: 0L // Jika folderName null atau tidak ditemukan, folderId diatur ke 0L (tanpa folder)

        val newNote = Note(nextId++, folderId, title, content, System.currentTimeMillis())
        notes.add(0, newNote) // Tambahkan ke paling atas
        Log.d(NOTE_REPOSITORY, "addNote() -> Note added to folderId: $folderId")
    }

    /**
     * Mengambil semua catatan, diurutkan dari yang terbaru.
     */
    fun getAllNotes(): List<Note> {
        return notes.sortedByDescending { it.timestamp }
    }

    /**
     * Mencari catatan berdasarkan ID-nya.
     */
    fun getNoteById(id: Long): Note? {
        return notes.find { it.id == id }
    }

    /**
     * Mengambil semua catatan yang termasuk dalam folder tertentu.
     */
    fun getNotesForFolder(folderId: Long): List<Note> {
        return notes.filter { it.folderId == folderId }.sortedByDescending { it.timestamp }
    }

    /**
     * Memperbarui judul, konten, dan folder dari catatan yang sudah ada.
     */
    fun updateNote(id: Long, newTitle: String, newContent: String, folderName: String? = null) {
        val noteToUpdate = getNoteById(id)
        noteToUpdate?.let {
            it.title = newTitle
            it.content = newContent
            it.timestamp = System.currentTimeMillis()
            folderName?.let { name ->
                val folder = folders.find { f -> f.name == name }
                if (folder != null) {
                    it.folderId = folder.id
                }
            }
        }
        Log.d(NOTE_REPOSITORY, "updateNote() -> Note has been updated")
    }

    /**
     * Menghapus catatan berdasarkan ID.
     */
    fun deleteNote(id: Long) {
        notes.removeAll { it.id == id }
        Log.d(NOTE_REPOSITORY, "deleteNote() -> Note has been deleted")
    }
}
