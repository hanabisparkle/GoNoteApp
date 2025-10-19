package com.example.gonoteapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.appcompat.widget.SearchView
import com.example.gonoteapp.MainActivity
import com.example.gonoteapp.NoteRepository
import com.example.gonoteapp.R
import com.example.gonoteapp.model.Note

/**
 * Fragment utama yang ditampilkan saat aplikasi pertama kali dibuka.
 * Menampilkan semua catatan dan menyediakan fungsionalitas pencarian serta multi-seleksi.
 * Merupakan turunan dari [BaseNoteListFragment].
 */
class MainFragment : BaseNoteListFragment() {

    private var isSelectionMode = false
    private lateinit var deleteButton: Button
    private lateinit var addToFolderButton: Button
    private lateinit var selectAllCheckbox: CheckBox

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Gunakan layout khusus untuk halaman utama
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState) // Panggil implementasi dari BaseNoteListFragment

        // Inisialisasi view yang spesifik untuk MainFragment
        val selectButton: Button = view.findViewById(R.id.select_button)
        deleteButton = view.findViewById(R.id.delete_button)
        addToFolderButton = view.findViewById(R.id.add_to_folder_button)
        selectAllCheckbox = view.findViewById(R.id.select_all_checkbox)
        val searchView: SearchView = view.findViewById(R.id.search_view)

        // Setup listener untuk search view
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterNotes(newText) // Filter daftar catatan saat teks pencarian berubah
                return true
            }
        })

        // Menangani logika untuk mengaktifkan/menonaktifkan mode seleksi
        selectButton.setOnClickListener {
            isSelectionMode = !isSelectionMode
            noteAdapter.setSelectionMode(isSelectionMode)

            if (isSelectionMode) {
                deleteButton.visibility = View.VISIBLE
                addToFolderButton.visibility = View.VISIBLE
                selectAllCheckbox.visibility = View.VISIBLE
            } else {
                deleteButton.visibility = View.GONE
                addToFolderButton.visibility = View.GONE
                selectAllCheckbox.visibility = View.GONE
                selectAllCheckbox.isChecked = false
                noteAdapter.deselectAll()
            }
        }

        // Menangani checkbox "Select All"
        selectAllCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) noteAdapter.selectAll() else noteAdapter.deselectAll()
        }

        // Menangani klik tombol "Add to Folder"
        addToFolderButton.setOnClickListener {
            val selectedNotes = noteAdapter.getSelectedNotes()
            if (selectedNotes.isNotEmpty()) {
                showFolderSelectDialog(selectedNotes)
            }
        }

        // Menangani klik tombol "Delete"
        deleteButton.setOnClickListener {
            val selectedNotes = noteAdapter.getSelectedNotes()
            if (selectedNotes.isNotEmpty()) {
                showDeleteSelectedNotesDialog(selectedNotes)
            }
        }
    }

    /**
     * Memfilter daftar catatan berdasarkan query pencarian.
     */
    private fun filterNotes(query: String?) {
        val allNotes = NoteRepository.getAllNotes()
        val notesToShow = if (query.isNullOrBlank()) {
            allNotes // Tampilkan semua jika query kosong
        } else {
            // Filter berdasarkan judul atau konten
            allNotes.filter {
                it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true)
            }
        }
        noteAdapter.setData(notesToShow)
        updateEmptyViewVisibility(notesToShow, R.string.empty_search_result) // Tampilkan pesan jika hasil pencarian kosong
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.updateTitle("Home") // Set judul toolbar
    }

    /**
     * Implementasi dari fungsi abstract di [BaseNoteListFragment].
     * Memuat semua catatan dari [NoteRepository].
     */
    override fun loadNotes() {
        val allNotes = NoteRepository.getAllNotes()
        noteAdapter.setData(allNotes)
        updateEmptyViewVisibility(allNotes, R.string.empty_notes) // Tampilkan pesan jika tidak ada catatan sama sekali
    }

    /**
     * Menampilkan dialog konfirmasi untuk menghapus catatan yang diseleksi.
     */
    private fun showDeleteSelectedNotesDialog(selectedNotes: Set<Note>){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete these notes?")
            .setPositiveButton("Delete") { _, _ ->
                selectedNotes.forEach { NoteRepository.deleteNote(it.id) }
                loadNotes() // Muat ulang daftar
                // Keluar dari mode seleksi setelah selesai
                isSelectionMode = false
                noteAdapter.setSelectionMode(false)
                deleteButton.visibility = View.GONE
                addToFolderButton.visibility = View.GONE
                selectAllCheckbox.visibility = View.GONE
                selectAllCheckbox.isChecked = false
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Menampilkan dialog untuk memilih folder tujuan saat memindahkan catatan.
     */
    private fun showFolderSelectDialog(selectedNotes: Set<Note>) {
        val folders = NoteRepository.getAllFolders()
        if (folders.isNotEmpty()) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Select folder")
                .setItems(folders.map { it.name }.toTypedArray()) { _, which ->
                    val selectedFolder = folders[which]
                    selectedNotes.forEach { it.folderId = selectedFolder.id }
                    Toast.makeText(requireContext(), "Notes moved to ${selectedFolder.name}", Toast.LENGTH_SHORT).show()
                    // Keluar dari mode seleksi setelah selesai
                    isSelectionMode = false
                    noteAdapter.setSelectionMode(false)
                    deleteButton.visibility = View.GONE
                    addToFolderButton.visibility = View.GONE
                    selectAllCheckbox.visibility = View.GONE
                    selectAllCheckbox.isChecked = false
                    loadNotes()
                }
                .show()
        } else {
            Toast.makeText(requireContext(), "No folders found. Create a folder first.", Toast.LENGTH_LONG).show()
        }
    }
}
