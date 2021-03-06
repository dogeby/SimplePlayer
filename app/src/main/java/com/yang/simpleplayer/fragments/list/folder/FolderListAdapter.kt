package com.yang.simpleplayer.fragments.list.folder

import android.content.ContentUris
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.yang.simpleplayer.R
import com.yang.simpleplayer.databinding.ViewFolderItemBinding
import com.yang.simpleplayer.models.Folder
import com.yang.simpleplayer.utils.Format
import com.yang.simpleplayer.utils.ImageLoader

class FolderListAdapter : RecyclerView.Adapter<FolderListAdapter.FolderViewHolder>(), Filterable {

    private val folders = mutableListOf<Folder>()
    private val filteredFolders = mutableListOf<Folder>()
    var itemViewOnclick: (String) -> Unit = {}
    var moreBtnOnclick: (Folder) -> Unit = {}

    fun updateFolders(folders: List<Folder>) {
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
                val filteringFolders = mutableListOf<Folder>()
                folders.forEach { folder ->
                    if(Format.getParentFolderName(folder.name).lowercase().contains(constraintString))
                        filteringFolders.add(folder)
                }
                filteringFolders
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            filteredFolders.clear()
            filteredFolders.addAll(results?.values as MutableList<Folder>)
            notifyDataSetChanged()
        }
    }

    inner class FolderViewHolder(binding: ViewFolderItemBinding): RecyclerView.ViewHolder(binding.root) {
        private val name = binding.name
        private val moreBtn = binding.moreBtn
        private val thumbnail = binding.thumbnail
        private val folderSize = binding.folderSize

        fun bind(item: Folder) {
            name.text = Format.getParentFolderName(item.name)
            itemView.setOnClickListener {
                itemViewOnclick(item.name)
            }

            val uri = ContentUris.withAppendedId(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, item.videoIds[0]
            )
            ImageLoader.loadThumbnail(uri, thumbnail)
            thumbnail.setBackgroundResource(R.color.white)
            thumbnail.scaleType = ImageView.ScaleType.CENTER_CROP
            folderSize.text = item.videoIds.size.toString()
            moreBtn.setOnClickListener { moreBtnOnclick(item) }
        }
    }
}