package com.example.gonoteapp

import android.content.Context
import android.util.Log
import com.example.gonoteapp.model.Folder
import com.example.gonoteapp.model.Note
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

/**
 * Repository tunggal (singleton) yang mengelola semua data catatan dan folder.
 * Bertindak sebagai sumber data utama (source of truth) untuk seluruh aplikasi.
 * Data disimpan secara lokal dalam file JSON untuk persistensi.
 */
class NoteRepository private constructor(private val context: Context) {

    private val gson = Gson()
    private val notesFile = File(context.filesDir, "notes.json")
    private val foldersFile = File(context.filesDir, "folders.json")

    private val notes = mutableListOf<Note>() // Daftar internal untuk semua catatan
    private val folders = mutableListOf<Folder>() // Daftar internal untuk semua folder
    private var nextId = 1L // Untuk auto-increment ID catatan
    private var nextFolderId = 1L // Untuk auto-increment ID folder

    companion object {
        private const val NOTE_REPOSITORY = "NOTE_REPOSITORY"

        @Volatile
        private var INSTANCE: NoteRepository? = null

        fun getInstance(context: Context): NoteRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: NoteRepository(context.applicationContext).also {
                    INSTANCE = it
                    it.loadData() // Load data saat instance pertama kali dibuat
                }
            }
        }
    }

    /**
     * Memuat data dari file JSON saat repository diinisialisasi.
     * Jika tidak ada file, data dummy akan dibuat.
     */
    private fun loadData() {
        loadFolders()
        loadNotes()

        if (folders.isEmpty() && notes.isEmpty()) {
            addDummyData()
        }

        // Pastikan ID berikutnya lebih tinggi dari ID maksimum yang ada
        nextId = (notes.maxOfOrNull { it.id } ?: 0L) + 1
        nextFolderId = (folders.maxOfOrNull { it.id } ?: 0L) + 1
    }

    // --- FUNGSI UNTUK FOLDER ---

    /**
     * Menambahkan folder baru dan menyimpan perubahan ke file.
     */
    fun addFolder(name: String) {
        folders.add(Folder(nextFolderId++, name))
        saveFolders() // Simpan perubahan
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
     * Memperbarui nama folder dan menyimpan perubahan ke file.
     */
    fun updateFolder(id: Long, newName: String) {
        val folderToUpdate = getFolderById(id)
        folderToUpdate?.name = newName
        saveFolders() // Simpan perubahan
        Log.d(NOTE_REPOSITORY, "updateFolder() -> Folder has been updated")
    }

    /**
     * Menghapus folder berdasarkan ID dan menyimpan perubahan ke file.
     */
    fun deleteFolder(id: Long) {
        folders.removeAll { it.id == id }
        saveFolders() // Simpan perubahan
        Log.d(NOTE_REPOSITORY, "deleteFolder() -> Folder has been deleted")
    }

    // --- FUNGSI UNTUK CATATAN ---

    /**
     * Menambahkan catatan baru dan menyimpan perubahan ke file.
     */
    fun addNote(title: String, content: String, folderName: String? = null) {
        val folderId = folderName?.let { name ->
            folders.find { it.name == name }?.id
        } ?: 0L

        val newNote = Note(nextId++, folderId, title, content, System.currentTimeMillis())
        notes.add(0, newNote)
        saveNotes() // Simpan perubahan
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
     * Memperbarui catatan dan menyimpan perubahan ke file.
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
        saveNotes() // Simpan perubahan
        Log.d(NOTE_REPOSITORY, "updateNote() -> Note has been updated")
    }

    /**
     * Menghapus catatan berdasarkan ID dan menyimpan perubahan ke file.
     */
    fun deleteNote(id: Long) {
        notes.removeAll { it.id == id }
        saveNotes() // Simpan perubahan
        Log.d(NOTE_REPOSITORY, "deleteNote() -> Note has been deleted")
    }

    // --- FUNGSI CACHING LOKAL (JSON) ---

    private fun saveNotes() {
        try {
            val jsonString = gson.toJson(notes)
            notesFile.writeText(jsonString)
            Log.d(NOTE_REPOSITORY, "Notes saved to file.")
        } catch (e: Exception) {
            Log.e(NOTE_REPOSITORY, "Error saving notes", e)
        }
    }

    private fun loadNotes() {
        if (notesFile.exists()) {
            try {
                val jsonString = notesFile.readText()
                if (jsonString.isNotBlank()) {
                    val type = object : TypeToken<MutableList<Note>>() {}.type
                    val loadedNotes: MutableList<Note> = gson.fromJson(jsonString, type)
                    notes.clear()
                    notes.addAll(loadedNotes)
                    Log.d(NOTE_REPOSITORY, "${notes.size} notes loaded from file.")
                }
            } catch (e: Exception) {
                Log.e(NOTE_REPOSITORY, "Error loading notes", e)
            }
        }
    }

    private fun saveFolders() {
        try {
            val jsonString = gson.toJson(folders)
            foldersFile.writeText(jsonString)
            Log.d(NOTE_REPOSITORY, "Folders saved to file.")
        } catch (e: Exception) {
            Log.e(NOTE_REPOSITORY, "Error saving folders", e)
        }
    }

    private fun loadFolders() {
        if (foldersFile.exists()) {
            try {
                val jsonString = foldersFile.readText()
                if (jsonString.isNotBlank()) {
                    val type = object : TypeToken<MutableList<Folder>>() {}.type
                    val loadedFolders: MutableList<Folder> = gson.fromJson(jsonString, type)
                    folders.clear()
                    folders.addAll(loadedFolders)
                    Log.d(NOTE_REPOSITORY, "${folders.size} folders loaded from file.")
                }
            } catch (e: Exception) {
                Log.e(NOTE_REPOSITORY, "Error loading folders", e)
            }
        }
    }

    /**
     * Blok inisialisasi yang dijalankan saat repository pertama kali dibuat.
     * Digunakan untuk mengisi data dummy agar aplikasi tidak kosong saat pertama kali dijalankan.
     */
    private fun addDummyData() {
        val folder1 = Folder(nextFolderId++, "Personal")
        val folder2 = Folder(nextFolderId++, "Work")
        folders.addAll(listOf(folder1, folder2))

        val initialNotes = listOf(
            Note(nextId++, folder2.id, "Meeting Notes", "Discuss Q3 budget and project timelines.", System.currentTimeMillis()),
            Note(nextId++, folder1.id, "Shopping List", "Milk, Bread, Eggs, Coffee.", System.currentTimeMillis()),
            Note(nextId++, folder1.id, "Book Ideas", "A story about a time-traveling librarian.", System.currentTimeMillis()),
            Note(nextId++, 0L, "Workout Plan", "Monday: Chest, Tuesday: Back, Wednesday: Legs.", System.currentTimeMillis()) // 0L berarti tidak ada folder
        )
        notes.addAll(initialNotes)

        // Simpan data dummy ke file saat pertama kali dibuat
        saveFolders()
        saveNotes()
        Log.d(NOTE_REPOSITORY, "Added dummy data and saved to files.")
    }
}
