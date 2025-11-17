package com.example.gonoteapp.fragments

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gonoteapp.FolderAdapter
import com.example.gonoteapp.MainActivity
import com.example.gonoteapp.NoteRepository
import com.example.gonoteapp.OnFolderClickListener
import com.example.gonoteapp.R
import com.example.gonoteapp.model.Folder
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Fragment untuk menampilkan daftar semua folder.
 * Mengelola logika untuk melihat, membuat, mengedit, dan menghapus folder.
 */
class FolderListFragment : Fragment(), OnFolderClickListener {
    private lateinit var foldersRecyclerView: RecyclerView
    private lateinit var foldersAdapter: FolderAdapter
    private lateinit var deleteButton: Button
    private lateinit var selectAllCheckbox: CheckBox
    private lateinit var emptyView: TextView
    private var isSelectionMode = false
    private val selectedFolders = mutableSetOf<Folder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout untuk fragment ini
        return inflater.inflate(R.layout.fragment_folder_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inisialisasi semua view
        foldersRecyclerView = view.findViewById(R.id.folders_recycler_view)
        emptyView = view.findViewById(R.id.folder_empty_view)
        val selectButton: Button = view.findViewById(R.id.folder_select_button)
        deleteButton = view.findViewById(R.id.folder_delete_button)
        selectAllCheckbox = view.findViewById(R.id.select_all_checkbox)

        // Setup adapter dan RecyclerView
        foldersAdapter = FolderAdapter(this)
        setupRecyclerView()
        setupItemTouchHelper()

        // Menangani logika untuk mode seleksi
        selectButton.setOnClickListener {
            isSelectionMode = !isSelectionMode
            foldersAdapter.setSelectionMode(isSelectionMode)
            if (isSelectionMode) {
                deleteButton.visibility = View.VISIBLE
                selectAllCheckbox.visibility = View.VISIBLE
            } else {
                deleteButton.visibility = View.GONE
                selectAllCheckbox.visibility = View.GONE
                selectAllCheckbox.isChecked = false
                selectedFolders.clear()
                foldersAdapter.deselectAll()
            }
        }

        // Menangani checkbox "Select All"
        selectAllCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                foldersAdapter.selectAll()
            } else {
                foldersAdapter.deselectAll()
            }
        }

        // Menangani tombol hapus untuk item yang diseleksi
        deleteButton.setOnClickListener {
            if (selectedFolders.isNotEmpty()) {
                showDeleteSelectedFoldersDialog(foldersAdapter.getSelectedFolders())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Update judul di MainActivity dan muat ulang data folder
        (activity as? MainActivity)?.updateTitle("Folders")
        loadFolders()
    }

    /**
     * Mengatur RecyclerView dengan adapter dan layout manager.
     */
    private fun setupRecyclerView() {
        foldersRecyclerView.adapter = foldersAdapter
        foldersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    /**
     * Memuat daftar folder dari [NoteRepository] dan menampilkannya di adapter.
     */
    private fun loadFolders() {
        val folders = NoteRepository.getAllFolders()
        foldersAdapter.setData(folders)
        // Tampilkan pesan jika daftar kosong
        if (folders.isEmpty()) {
            foldersRecyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            foldersRecyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
        }
    }

    /**
     * Dipanggil saat sebuah folder di-klik.
     * Membuka [FolderNotesFragment] untuk menampilkan catatan di dalam folder tersebut.
     */
    override fun onFolderClicked(folder: Folder) {
        (activity as? MainActivity)?.onFolderSelected(folder.name) // Simpan nama folder di MainActivity
        val fragment = FolderNotesFragment.newInstance(folder.id)
        parentFragmentManager.beginTransaction()
            .replace(R.id.my_fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    /**
     * Dipanggil saat status seleksi sebuah folder berubah.
     */
    override fun onFolderSelected(folder: Folder, isSelected: Boolean) {
        if (isSelected) {
            selectedFolders.add(folder)
        } else {
            selectedFolders.remove(folder)
        }
    }

    /**
     * Mengatur ItemTouchHelper untuk swipe-to-delete dan swipe-to-edit.
     */
    private fun setupItemTouchHelper() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false // Tidak digunakan
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val folder = foldersAdapter.getFolderAt(position)

                if (direction == ItemTouchHelper.LEFT) { // Swipe kiri untuk hapus
                    showDeleteFolderConfirmationDialog(folder, viewHolder)
                } else { // Swipe kanan untuk edit
                    showEditFolderDialog(folder)
                    foldersAdapter.notifyItemChanged(position) // Reset tampilan item
                }
            }
            // ... (kode untuk visual swipe tetap sama)
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(foldersRecyclerView)
    }

    /**
     * Menampilkan dialog untuk mengedit nama folder.
     */
    private fun showEditFolderDialog(folder: Folder) {
        val editText = EditText(requireContext()).apply { setText(folder.name) }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Folder Name")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val newName = editText.text.toString()
                if (newName.isNotBlank() && newName != folder.name) {
                    NoteRepository.updateFolder(folder.id, newName)
                    loadFolders() // Muat ulang data
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Menampilkan dialog konfirmasi sebelum menghapus satu folder.
     */
    private fun showDeleteFolderConfirmationDialog(folder: Folder, viewHolder: RecyclerView.ViewHolder) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Folder")
            .setMessage("Are you sure you want to delete '${folder.name}'? Notes inside will become uncategorized.")
            .setPositiveButton("Delete") { _, _ ->
                NoteRepository.deleteFolder(folder.id)
                loadFolders()
            }
            .setNegativeButton("Cancel") { _, _ -> foldersAdapter.notifyItemChanged(viewHolder.adapterPosition) }
            .setOnCancelListener { foldersAdapter.notifyItemChanged(viewHolder.adapterPosition) }
            .show()
    }

    /**
     * Menampilkan dialog konfirmasi sebelum menghapus beberapa folder yang diseleksi.
     */
    private fun showDeleteSelectedFoldersDialog(selectedFolders: Set<Folder>){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Selected Folders")
            .setMessage("Are you sure you want to permanently delete the selected folders? Any notes within them will be uncategorized.")
            .setPositiveButton("Delete") { _, _ ->
                selectedFolders.forEach { folder ->
                    NoteRepository.getNotesForFolder(folder.id).forEach { it.folderId = 0L } // Set folderId ke 0 (uncategorized)
                    NoteRepository.deleteFolder(folder.id)
                }
                loadFolders()
                // Keluar dari mode seleksi setelah selesai
                isSelectionMode = false
                foldersAdapter.setSelectionMode(false)
                deleteButton.visibility = View.GONE
                selectAllCheckbox.visibility = View.GONE
                selectAllCheckbox.isChecked = false
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
