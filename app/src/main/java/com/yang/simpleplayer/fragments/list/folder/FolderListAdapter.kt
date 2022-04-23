package com.yang.simpleplayer.fragments.list.folder

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yang.simpleplayer.R
import com.yang.simpleplayer.databinding.ViewFolderItemBinding
import com.yang.simpleplayer.databinding.ViewVideoItemBinding
import com.yang.simpleplayer.models.Folder
import com.yang.simpleplayer.models.Video
import com.yang.simpleplayer.utils.Format
import com.yang.simpleplayer.utils.ImageLoader

class FolderListAdapter : RecyclerView.Adapter<FolderListAdapter.FolderViewHolder>() {

    private val folders = mutableListOf<Folder>()
    var itemViewOnclick: (LongArray) -> Unit = {}
    var moreBtnOnclick: (Folder) -> Unit = {}

    fun updateFolders(folders: List<Folder>) {
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

        fun bind(item: Folder) {
            name.text = item.name
            itemView.setOnClickListener {
                val videos = item.videoFiles.toList()
                val videoIds = LongArray(videos.size) { videos[it].id }
                itemViewOnclick(videoIds)
            }
            moreBtn.setOnClickListener { moreBtnOnclick(item) }
            // TODO:  폴더리스트어댑터 holder.thumbnails 작성하기
        }
    }
}