package com.example.gonoteapp

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gonoteapp.model.Folder
import com.example.gonoteapp.model.Note

class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    // views for the folder data that we want to show
    private val name: TextView = itemView.findViewById(R.id.folder_name_textview)

    // bind data to the views
    fun bindData(folder: Folder) {
        name.text = folder.name

        // TODO: onclicklistemer for display notes in folder
    }
}