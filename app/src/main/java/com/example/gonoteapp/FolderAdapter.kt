package com.example.gonoteapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gonoteapp.model.Folder

interface OnFolderClickListener {
    fun onFolderClicked(folder: Folder)
}

class FolderAdapter () : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

    private var folders = listOf<Folder>()

    class FolderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val folderName: TextView = view.findViewById(R.id.folder_name_textview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.folder_list_item, parent, false)
            return FolderViewHolder(view)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = folders[position]

        holder.folderName.text = folder.name
    }

    override fun getItemCount(): Int {
        return folders.size
    }

    fun setData(newFolders: List<Folder>) {
        folders = newFolders
        notifyDataSetChanged()
    }
}