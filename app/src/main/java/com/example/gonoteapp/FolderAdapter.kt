package com.example.gonoteapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gonoteapp.model.Folder
import com.example.gonoteapp.model.Note


class FolderAdapter (
    private var folders: List<Folder>,
    private val listener: OnFolderClickListener
    ) : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

    interface OnFolderClickListener {
        fun onFolderClick(folder: Folder)
    }

    class FolderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val folderName: TextView = view.findViewById(R.id.folder_name_textview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.folder_list_item, parent, false)
            return FolderViewHolder(view)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = folders[position]

        holder.folderName.text = folder.name
        holder.itemView.setOnClickListener {
            listener.onFolderClick(folder)
        }
    }

    override fun getItemCount(): Int {
        return folders.size
    }

    fun getFolderAt(position: Int): Folder {
        return folders[position]
    }

    fun setData(newFolders: List<Folder>) {
        this.folders = newFolders
        notifyDataSetChanged()
    }
}