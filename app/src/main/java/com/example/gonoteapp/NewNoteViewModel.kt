package com.example.gonoteapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gonoteapp.model.Note

/**
 * ViewModel untuk [NewNoteFragment].
 * Mengelola logika untuk membuat catatan baru atau mengedit catatan yang sudah ada.
 */
class NewNoteViewModel : ViewModel() {

    // ID dari catatan yang akan diedit. Defaultnya -1L, menandakan mode pembuatan baru.
    private var noteIdToEdit: Long = -1L

    /**
     * Properti untuk memeriksa apakah sedang dalam mode edit.
     * Bernilai true jika `noteIdToEdit` bukan -1L.
     */
    val isEditMode: Boolean
        get() = noteIdToEdit != -1L

    // LiveData internal untuk menampung data catatan yang akan diedit.
    private val _note = MutableLiveData<Note?>()
    // LiveData yang akan diobservasi oleh Fragment untuk mendapatkan data catatan.
    val note: LiveData<Note?> = _note

    // LiveData internal untuk memberi sinyal navigasi kembali.
    private val _navigateBack = MutableLiveData<Boolean>()
    // LiveData yang akan diobservasi oleh Fragment untuk trigger navigasi kembali setelah menyimpan.
    val navigateBack: LiveData<Boolean> = _navigateBack

    /**
     * Memuat data catatan dari repository berdasarkan ID.
     * Dipanggil oleh Fragment saat membuka catatan yang sudah ada untuk diedit.
     */
    fun loadNote(noteId: Long) {
        if (noteId == -1L) return // Keluar jika tidak ada ID yang valid (bukan mode edit)
        noteIdToEdit = noteId

        // Mengambil data catatan dari repository dan menyimpannya di LiveData.
        _note.value = NoteRepository.getNoteById(noteId)
    }

    /**
     * Menyimpan catatan (baru atau yang sudah ada).
     * Memeriksa `isEditMode` untuk menentukan apakah harus membuat baru atau memperbarui.
     */
    fun saveNote(title: String, content: String, folderName: String? = null) {
        if (isEditMode) {
            NoteRepository.updateNote(noteIdToEdit, title, content, folderName)
        } else {
            NoteRepository.addNote(title, content, folderName)
        }
        // Memberi sinyal ke Fragment untuk navigasi kembali.
        _navigateBack.value = true
    }

    /**
     * Mereset sinyal navigasi setelah Fragment selesai menavigasi.
     * Mencegah navigasi berulang jika terjadi perubahan konfigurasi.
     */
    fun onNavigateBackComplete() {
        _navigateBack.value = false
    }
}
