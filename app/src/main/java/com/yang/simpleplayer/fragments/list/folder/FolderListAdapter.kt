package com.yang.simpleplayer.fragments.list.folder

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.yang.simpleplayer.R
import com.yang.simpleplayer.databinding.ViewFolderItemBinding
import com.yang.simpleplayer.utils.Format

class FolderListAdapter : RecyclerView.Adapter<FolderListAdapter.FolderViewHolder>(), Filterable {

    private val folders = mutableListOf<String>()
    private val filteredFolders = mutableListOf<String>()
    var itemViewOnclick: (String) -> Unit = {}
    var moreBtnOnclick: (String) -> Unit = {}

    fun updateFolders(folders: List<String>) {
        filteredFolders.clear()
        filteredFolders.addAll(folders)
        this.folders.clear()
        this.folders.addAll(folders)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.view_folder_item, parent, false)
        val binding = ViewFolderItemBinding.bind(view)
        return FolderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = filteredFolders[position]
        holder.bind(folder)
    }

    override fun getItemCount() = filteredFolders.size

    override fun getFilter(): Filter {
        return ItemFilter()
    }

    private inner class ItemFilter:Filter() {
        override fun performFiltering(constraint: CharSequence?) = FilterResults().apply {
            val constraintString = constraint.toString().lowercase()
            values = if(constraintString.isNullOrBlank()) {
                folders
            }
            else {
                val filteringFolders = mutableListOf<String>()
                folders.forEach { folderName ->
                    if(Format.getParentFolderName(folderName).lowercase().contains(constraintString))
                        filteringFolders.add(folderName)
                }
                filteringFolders
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            filteredFolders.clear()
            filteredFolders.addAll(results?.values as MutableList<String>)
            notifyDataSetChanged()
        }
    }

    inner class FolderViewHolder(binding: ViewFolderItemBinding): RecyclerView.ViewHolder(binding.root) {
        private val name = binding.name
        private val moreBtn = binding.moreBtn

        fun bind(item: String) {
            name.text = Format.getParentFolderName(item)
            itemView.setOnClickListener {
                itemViewOnclick(item)
            }
            moreBtn.setOnClickListener { moreBtnOnclick(item) }
        }
    }
}