package com.example.gonoteapp.fragments

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.Callback.getDefaultUIUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gonoteapp.FolderAdapter
import com.example.gonoteapp.MainActivity
import com.example.gonoteapp.NoteRepository
import com.example.gonoteapp.R
import com.example.gonoteapp.model.Folder

class FolderListFragment : Fragment(), FolderAdapter.OnFolderClickListener, NoteRepository.OnDataChangeListener {
    private lateinit var foldersRecyclerView: RecyclerView
    private lateinit var foldersAdapter: FolderAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        foldersRecyclerView = view.findViewById(R.id.folders_recycler_view)
        foldersAdapter = FolderAdapter(NoteRepository.getAllFolders(), this)
        setupRecyclerView()

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) : Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val folder = foldersAdapter.getFolderAt(position)

                if (direction == ItemTouchHelper.LEFT) {
                    showDeleteFolderConfirmationDialog(folder, viewHolder)
                } else {
                    showEditFolderDialog(folder)
                    foldersAdapter.notifyItemChanged(position)
                }
            }
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val foregroundView = viewHolder.itemView.findViewById<View>(R.id.card_view_folder)
                val editLayout = viewHolder.itemView.findViewById<View>(R.id.edit_action_layout_folder)
                val deleteLayout = viewHolder.itemView.findViewById<View>(R.id.delete_action_layout_folder)

                if (dX > 0) {
                    editLayout.visibility = View.VISIBLE
                    deleteLayout.visibility = View.GONE
                } else if (dX < 0) {
                    editLayout.visibility = View.GONE
                    deleteLayout.visibility = View.VISIBLE
                } else {
                    editLayout.visibility = View.GONE
                    deleteLayout.visibility = View.GONE
                }

                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive)
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                val foregroundView = viewHolder.itemView.findViewById<View>(R.id.card_view_folder)
                val editLayout = viewHolder.itemView.findViewById<View>(R.id.edit_action_layout_folder)
                val deleteLayout = viewHolder.itemView.findViewById<View>(R.id.delete_action_layout_folder)

                editLayout.visibility = View.GONE
                deleteLayout.visibility = View.GONE

                getDefaultUIUtil().clearView(foregroundView)
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(foldersRecyclerView)
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDataChanged() {
        loadFolders()
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.updateTitle("Folders")
        loadFolders()
    }

    private fun showEditFolderDialog(folder: Folder) {
        val context = requireContext()
        val editText = EditText(context).apply {
            setText(folder.name)
        }

        AlertDialog.Builder(context)
            .setTitle("Edit Folder Name")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val newName = editText.text.toString()
                if (newName.isNotBlank() && newName != folder.name) {
                    NoteRepository.updateFolder(folder.id, newName)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteFolderConfirmationDialog(folder: Folder, viewHolder: RecyclerView.ViewHolder) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Folder")
            .setMessage("Are you sure you want to delete '${folder.name}'? Notes inside will become uncategorized.")
            .setPositiveButton("Delete") { _, _ ->
                NoteRepository.deleteFolder(folder.id)
                foldersAdapter = FolderAdapter(NoteRepository.getAllFolders(), this)
                foldersRecyclerView.adapter = foldersAdapter
                loadFolders()
            }
            .setNegativeButton("Cancel") { _, _ ->
                foldersAdapter.notifyItemChanged(viewHolder.adapterPosition)
            }
            .setOnCancelListener { 
                foldersAdapter.notifyItemChanged(viewHolder.adapterPosition)
            }
            .show()
    }

    private fun setupRecyclerView() {
        foldersRecyclerView.adapter = foldersAdapter
        foldersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadFolders() {
        val folders = NoteRepository.getAllFolders()
        foldersAdapter.setData(folders)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_folder_list, container, false)
    }

    override fun onFolderClick(folder: Folder) {
        (activity as? MainActivity)?.onFolderSelected(folder.name)
        val fragment = FolderNotesFragment.newInstance(folder.id)
        parentFragmentManager.beginTransaction()
            .replace(R.id.my_fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun showNewFolderDialog() {
        val editText = EditText(requireContext()).apply {
            hint = "Folder Name"
        }

        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 40, 60, 20)
            addView(editText)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("New Folder")
            .setView(layout)
            .setPositiveButton("Create") { _, _ ->
                val folderName = editText.text.toString()
                if (folderName.isNotBlank()) {
                    NoteRepository.addFolder(folderName)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
