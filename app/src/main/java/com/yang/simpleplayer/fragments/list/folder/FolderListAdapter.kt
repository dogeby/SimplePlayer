package com.yang.simpleplayer.fragments.list.folder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yang.simpleplayer.R
import com.yang.simpleplayer.databinding.ViewFolderItemBinding
import com.yang.simpleplayer.utils.Format

class FolderListAdapter : RecyclerView.Adapter<FolderListAdapter.FolderViewHolder>() {

    private val folders = mutableListOf<String>()
    var itemViewOnclick: (String) -> Unit = {}
    var moreBtnOnclick: (String) -> Unit = {}

    fun updateFolders(folders: List<String>) {
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
        val folder = folders[position]
        holder.bind(folder)
    }

    override fun getItemCount() = folders.size

    inner class FolderViewHolder(binding: ViewFolderItemBinding): RecyclerView.ViewHolder(binding.root) {
        private val thumbnails = binding.thumbnail
        private val name = binding.name
        private val moreBtn = binding.moreBtn

        fun bind(item: String) {
            name.text = Format.getParentFolderName(item)
            itemView.setOnClickListener {
                itemViewOnclick(item)
            }
            moreBtn.setOnClickListener { moreBtnOnclick(item) }
            // TODO: holder.thumbnails 작성
        }
    }
}