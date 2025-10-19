package com.example.gonoteapp

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gonoteapp.model.Folder

// Using the corrected name: FolderViewHolder
class FolderViewHolder(itemView: View, private val listener: OnFolderClickListener) : RecyclerView.ViewHolder(itemView) {

    // Assuming you have these IDs in your folder_list_item.xml
    private val folderName: TextView = itemView.findViewById(R.id.folder_name_textview)
    private val checkbox: CheckBox = itemView.findViewById(R.id.folder_checkbox)

    fun bindFolderData(folder: Folder, isSelectionMode: Boolean) {
        folderName.text = folder.name

        // Show/hide checkbox based on selection mode
        if (isSelectionMode) {
            checkbox.visibility = View.VISIBLE
        } else {
            checkbox.visibility = View.GONE
            checkbox.isChecked = false // Reset state when not in selection mode
        }

        // Handle clicks on the entire item
        itemView.setOnClickListener {
            if (isSelectionMode) {
                // In selection mode, a click toggles the checkbox
                checkbox.isChecked = !checkbox.isChecked
            } else {
                // Otherwise, it opens the folder
                listener.onFolderClicked(folder)
            }
        }

        // Listen for when the checkbox state changes
        checkbox.setOnCheckedChangeListener { _, isChecked ->
            listener.onFolderSelected(folder, isChecked)
        }
    }
}