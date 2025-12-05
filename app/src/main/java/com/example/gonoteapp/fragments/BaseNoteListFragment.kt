package com.example.gonoteapp.fragments

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
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
import com.example.gonoteapp.model.Folder
import com.example.gonoteapp.model.Note
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Kelas dasar (abstract) untuk semua fragment yang menampilkan daftar catatan.
 * Mengandung logika umum seperti setup RecyclerView, swipe-to-delete/edit, dan penanganan klik.
 * Kelas turunan (seperti MainFragment) hanya perlu mengimplementasikan `loadNotes()`.
 */
abstract class BaseNoteListFragment : Fragment(), OnNoteClickListener{
    protected lateinit var notesRecyclerView: RecyclerView
    protected lateinit var noteAdapter: NotePreviewAdapter
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
        val repository = NoteRepository.getInstance(requireContext())
        // Inisialisasi adapter dengan listener dari fragment ini.
        noteAdapter = NotePreviewAdapter(this, repository)
        setupRecyclerView()
        loadNotes() // Memuat data catatan (implementasi ada di kelas turunan)
    }

    override fun onResume() {
        super.onResume()
        loadNotes() // Muat ulang catatan setiap kali fragment kembali ditampilkan
    }

    override fun onNoteLongHold(note: Note) {
        val position = noteAdapter.getNotePosition(note)

        if (position == -1) return // Note not found, do nothing

        // 2. Use the position to find the ViewHolder from the RecyclerView.
        //    The 'findViewHolderForAdapterPosition' method is the key here.
        val viewHolder = notesRecyclerView.findViewHolderForAdapterPosition(position)

        // 3. If the ViewHolder is found (it should be), get its itemView.
        viewHolder?.let { holder ->
            val anchorView = holder.itemView
            // 4. Now you have both the note and the anchor view.
            //    Call the adapter's function.
            noteAdapter.showLongHoldMenu(note, anchorView)
        }
    }

    override fun onGoToFolderClicked(note: Note, folder: Folder){
        // Logika navigasi untuk pindah ke FolderNotesFragment
        val frag = FolderNotesFragment().apply {
            arguments = Bundle().apply {
                putLong("FOLDER_ID", folder.id)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.my_fragment_container, frag)
            .addToBackStack(null)
            .commit()
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
     * Mendelegasikan logika penambahan/penghapusan ke adapter.
     */
    override fun onNoteSelected(note: Note, isSelected: Boolean) {
        if (isSelected) {
            noteAdapter.addSelected(note)
        } else {
            noteAdapter.removeSelected(note)
        }
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

        // Konfigurasi until hold gesture




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

            
            // Menggambar ikon dan latar belakang saat item digeser
            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                val itemView = viewHolder.itemView
                val background = ColorDrawable()
                val icon: android.graphics.drawable.Drawable?

                // Geser ke kanan (Edit)
                if (dX > 0) {
                    icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_edit)
                    background.color = ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark)
                    background.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
                    background.draw(c)

                    val iconTop = itemView.top + (itemView.height - (icon?.intrinsicHeight ?: 0)) / 2
                    val iconMargin = (itemView.height - (icon?.intrinsicHeight ?: 0)) / 2
                    val iconLeft = itemView.left + iconMargin
                    val iconRight = itemView.left + iconMargin + (icon?.intrinsicWidth ?: 0)
                    val iconBottom = iconTop + (icon?.intrinsicHeight ?: 0)
                    icon?.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    icon?.draw(c)
                }
                // Geser ke kiri (Delete)
                else if (dX < 0) {
                    icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete)
                    background.color = ContextCompat.getColor(requireContext(), android.R.color.holo_red_light)
                    background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    background.draw(c)

                    val iconTop = itemView.top + (itemView.height - (icon?.intrinsicHeight ?: 0)) / 2
                    val iconMargin = (itemView.height - (icon?.intrinsicHeight ?: 0)) / 2
                    val iconLeft = itemView.right - iconMargin - (icon?.intrinsicWidth ?: 0)
                    val iconRight = itemView.right - iconMargin
                    val iconBottom = iconTop + (icon?.intrinsicHeight ?: 0)
                    icon?.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    icon?.draw(c)
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(notesRecyclerView)
    }

    /**
     * Menampilkan dialog konfirmasi sebelum menghapus catatan.
     */
//    private fun showDeleteConfirmationDialog(note: Note, viewHolder: RecyclerView.ViewHolder) {
//        AlertDialog.Builder(requireContext())
//            .setTitle("Delete Note")
//            .setMessage("Are you sure you want to delete this note?")
//            .setPositiveButton("Delete") { _, _ ->
//                NoteRepository.deleteNote(note.id)
//                loadNotes() // Muat ulang daftar setelah hapus
//            }
//            .setNegativeButton("Cancel") { _, _ ->
//                // Kembalikan item yang di-swipe jika dibatalkan
//                noteAdapter.notifyItemChanged(viewHolder.adapterPosition)
//            }
//            .setOnCancelListener {
//                noteAdapter.notifyItemChanged(viewHolder.adapterPosition)
//            }
//            .create()
//            .show()
//    }

    private fun showDeleteConfirmationDialog(note: Note, viewHolder: RecyclerView.ViewHolder) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete the note: '${note.title}'?")
            .setPositiveButton("Delete") { _, _ ->
                NoteRepository.getInstance(requireContext()).deleteNote(note.id)
                loadNotes()
            }
            .setNegativeButton("Cancel") { _, _ -> noteAdapter.notifyItemChanged(viewHolder.adapterPosition) }
            .setOnCancelListener { noteAdapter.notifyItemChanged(viewHolder.adapterPosition) }
            .show()
    }

    /**
     * Fungsi abstract yang harus diimplementasikan oleh kelas turunan.
     * Bertanggung jawab untuk memuat data catatan yang relevan dari [NoteRepository].
     */
    protected abstract fun loadNotes()
}
