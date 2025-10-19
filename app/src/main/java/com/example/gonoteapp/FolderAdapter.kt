package com.example.gonoteapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gonoteapp.model.Folder

// This interface is the bridge between the adapter and the fragment
interface OnFolderClickListener {
    fun onFolderClicked(folder: Folder)
    fun onFolderSelected(folder: Folder, isSelected: Boolean)
}

class FolderAdapter(
    private val listener: OnFolderClickListener
) : RecyclerView.Adapter<FolderViewHolder>() { // Using the corrected ViewHolder name

    private val folders = mutableListOf<Folder>()
    private val selectedFolders = mutableSetOf<Folder>()
    private var selectionMode = false

    // Toggles selection mode
    fun setSelectionMode(isActive: Boolean) {
        selectionMode = isActive
        if (!isActive) {
            selectedFolders.clear() // Clear selections when exiting selection mode
        }
        notifyDataSetChanged() // Redraw the list
    }
    
    // Allows the fragment to get the list of selected folders
    fun getSelectedFolders(): Set<Folder> {
        return selectedFolders
    }

    // Standard method to update the data in the list
    fun setData(newFolders: List<Folder>) {
        folders.clear()
        folders.addAll(newFolders)
        notifyDataSetChanged()
    }
    
    // Methods for the fragment to tell the adapter about selections
    fun addSelected(folder: Folder) {
        selectedFolders.add(folder)
    }

    fun removeSelected(folder: Folder) {
        selectedFolders.remove(folder)
    }

    fun selectAll() {
        selectedFolders.clear()
        selectedFolders.addAll(folders)
        notifyDataSetChanged()
    }

    fun deselectAll() {
        selectedFolders.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.folder_list_item, parent, false)
        return FolderViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        // Pass the data and selection state to the ViewHolder
        val folder = folders[position]
        val isSelected = selectedFolders.contains(folder)
        holder.bindFolderData(folder, selectionMode, isSelected)
    }

    override fun getItemCount(): Int {
        return folders.size
    }
    
    fun getFolderAt(position: Int): Folder {
        return folders[position]
    }
}