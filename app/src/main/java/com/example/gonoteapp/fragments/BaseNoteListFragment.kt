package com.example.gonoteapp.fragments

import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gonoteapp.NotePreviewAdapter
import com.example.gonoteapp.NoteRepository
import com.example.gonoteapp.OnNoteClickListener
import com.example.gonoteapp.R
import com.example.gonoteapp.model.Note

/**
 * Kelas dasar (abstract) untuk semua fragment yang menampilkan daftar catatan.
 * Mengandung logika umum seperti setup RecyclerView, swipe-to-delete/edit, dan penanganan klik.
 * Kelas turunan (seperti MainFragment) hanya perlu mengimplementasikan `loadNotes()`.
 */
abstract class BaseNoteListFragment : Fragment(), OnNoteClickListener {
    protected lateinit var notesRecyclerView: RecyclerView
    protected lateinit var noteAdapter: NotePreviewAdapter
    private val selectedNotes = mutableSetOf<Note>()
    private var emptyView: TextView? = null // Tampilan untuk saat daftar kosong

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Layout yang digunakan oleh kelas dasar ini.
        return inflater.inflate(R.layout.fragment_base_note_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notesRecyclerView = view.findViewById(R.id.notes_recycler_view)
        emptyView = view.findViewById(R.id.note_empty_view)

        // Inisialisasi adapter dengan listener dari fragment ini.
        noteAdapter = NotePreviewAdapter(this)
        setupRecyclerView()
        loadNotes() // Memuat data catatan (implementasi ada di kelas turunan)
    }

    override fun onResume() {
        super.onResume()
        loadNotes() // Muat ulang catatan setiap kali fragment kembali ditampilkan
    }

    /**
     * Dipanggil saat sebuah catatan di-klik (bukan dalam mode seleksi).
     * Membuka [NoteFullViewFragment] untuk menampilkan detail catatan.
     */
    override fun onNoteClicked(note: Note) {
        val fragment = NoteFullViewFragment()
        fragment.arguments = bundleOf("NOTE_ID" to note.id)

        parentFragmentManager.beginTransaction()
            .replace(R.id.my_fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    /**
     * Dipanggil saat status seleksi sebuah catatan berubah (melalui checkbox).
     * Menambah atau menghapus catatan dari daftar `selectedNotes`.
     */
    override fun onNoteSelected(note: Note, isSelected: Boolean) {
        if (isSelected) {
            selectedNotes.add(note)
        } else {
            selectedNotes.remove(note)
        }
    }

    /**
     * Mengembalikan daftar catatan yang sedang diseleksi.
     * Dipanggil oleh [MainFragment] untuk aksi massal seperti hapus atau pindah folder.
     */
    fun getSelectedNotes(): Set<Note> {
        return selectedNotes
    }

    /**
     * Menampilkan atau menyembunyikan `emptyView` jika daftar catatan kosong.
     * @param list Daftar data (catatan atau folder).
     * @param emptyTextResId Resource ID dari teks yang akan ditampilkan.
     */
    protected fun updateEmptyViewVisibility(list: List<Any>, emptyTextResId: Int) {
        if (list.isEmpty()) {
            notesRecyclerView.visibility = View.GONE
            emptyView?.visibility = View.VISIBLE
            emptyView?.setText(emptyTextResId)
        } else {
            notesRecyclerView.visibility = View.VISIBLE
            emptyView?.visibility = View.GONE
        }
    }

    /**
     * Mengatur RecyclerView, termasuk LayoutManager dan ItemTouchHelper untuk swipe.
     */
    private fun setupRecyclerView() {
        notesRecyclerView.adapter = noteAdapter
        notesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Konfigurasi untuk swipe gestures
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false // Tidak digunakan
            }

            // Dipanggil saat item di-swipe
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val note = noteAdapter.getNoteAt(position)

                when (direction) {
                    ItemTouchHelper.LEFT -> { // Swipe ke kiri untuk menghapus
                        showDeleteConfirmationDialog(note, viewHolder)
                    }
                    ItemTouchHelper.RIGHT -> { // Swipe ke kanan untuk mengedit
                        val fragment = NewNoteFragment()
                        fragment.arguments = bundleOf("NOTE_ID_TO_EDIT" to note.id)

                        parentFragmentManager.beginTransaction()
                            .replace(R.id.my_fragment_container, fragment)
                            .addToBackStack(null)
                            .commit()
                    }
                }
            }
            // ... (kode untuk visual swipe tetap sama)
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(notesRecyclerView)
    }

    /**
     * Menampilkan dialog konfirmasi sebelum menghapus catatan.
     */
    private fun showDeleteConfirmationDialog(note: Note, viewHolder: RecyclerView.ViewHolder) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Delete") { _, _ ->
                NoteRepository.deleteNote(note.id)
                loadNotes() // Muat ulang daftar setelah hapus
            }
            .setNegativeButton("Cancel") { _, _ ->
                // Kembalikan item yang di-swipe jika dibatalkan
                noteAdapter.notifyItemChanged(viewHolder.adapterPosition)
            }
            .setOnCancelListener {
                noteAdapter.notifyItemChanged(viewHolder.adapterPosition)
            }
            .create()
            .show()
    }

    /**
     * Fungsi abstract yang harus diimplementasikan oleh kelas turunan.
     * Bertanggung jawab untuk memuat data catatan yang relevan dari [NoteRepository].
     */
    protected abstract fun loadNotes()
}
