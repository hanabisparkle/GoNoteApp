package com.example.gonoteapp.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.example.gonoteapp.MainActivity
import com.example.gonoteapp.NoteRepository
import com.example.gonoteapp.R
import com.example.gonoteapp.model.Note
import kotlin.properties.Delegates

/**
 * Fragment untuk menampilkan daftar catatan yang ada di dalam sebuah folder spesifik.
 * Merupakan turunan dari [BaseNoteListFragment] dan mengimplementasikan `loadNotes`.
 */
class FolderNotesFragment : BaseNoteListFragment() {

    private lateinit var folderName: String
    private var folderId by Delegates.notNull<Long>() // ID folder yang akan ditampilkan

    /**
     * Dipanggil saat fragment dibuat. Mengambil folderId dari arguments.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            folderId = it.getLong(ARG_FOLDER_ID)
        }
    }

    /**
     * Dipanggil setelah view dibuat. Mengatur judul toolbar dan tombol kembali.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repository = NoteRepository.getInstance(requireContext())
        folderName = repository.getFolderById(folderId)?.name ?: "Unknown Folder"
        // Meminta MainActivity untuk memperbarui judul di toolbar.
        (activity as? MainActivity)?.updateTitle(folderName)

        // Menampilkan tombol kembali (panah) di toolbar.
        val toolbar = activity?.findViewById<Toolbar>(R.id.toolbar)
        toolbar?.setNavigationIcon(R.drawable.ic_back)
        toolbar?.setNavigationOnClickListener {
            parentFragmentManager.popBackStack() // Kembali ke layar sebelumnya
        }
    }

    override fun onResume() {
        super.onResume()
        // Pastikan judul toolbar benar saat fragment kembali ditampilkan.
        (activity as? MainActivity)?.updateTitle(folderName)
    }

    /**
     * Dipanggil saat view dihancurkan. Membersihkan listener pada toolbar.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        // Hapus ikon dan listener navigasi untuk mencegah kebocoran memori atau perilaku tak terduga.
        val toolbar = activity?.findViewById<Toolbar>(R.id.toolbar)
        toolbar?.navigationIcon = null
        toolbar?.setNavigationOnClickListener(null)
    }

    /**
     * Implementasi dari fungsi abstract di [BaseNoteListFragment].
     * Memuat catatan dari [NoteRepository] yang sesuai dengan `folderId`.
     */
    override fun loadNotes() {
        val repository = NoteRepository.getInstance(requireContext())
        val notes = repository.getNotesForFolder(folderId)
        noteAdapter.setData(notes)
        // Menampilkan pesan jika tidak ada catatan di folder ini.
        updateEmptyViewVisibility(notes, R.string.empty_notes)
    }

    override fun onNoteLongHold(note: Note) {
        TODO("Not yet implemented")
    }

    /**
     * Companion object untuk membuat instance baru dari fragment ini dengan cara yang aman.
     * Ini adalah pola pabrik (factory pattern) yang direkomendasikan untuk fragment.
     */
    companion object {
        private const val ARG_FOLDER_ID = "folder_id"

        /**
         * Membuat instance baru dari [FolderNotesFragment].
         * @param folderId ID dari folder yang catatannya akan ditampilkan.
         */
        fun newInstance(folderId: Long): FolderNotesFragment {
            val fragment = FolderNotesFragment()
            val args = Bundle()
            args.putLong(ARG_FOLDER_ID, folderId)
            fragment.arguments = args
            return fragment
        }
    }
}
