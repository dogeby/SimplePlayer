package com.yang.simpleplayer.fragments.list.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.yang.simpleplayer.R
import com.yang.simpleplayer.databinding.ViewPlaylistItemBinding
import com.yang.simpleplayer.models.PlaylistWithVideoInfo

// TODO: PlayListAdapter 작성
class PlaylistListAdapter:RecyclerView.Adapter<PlaylistListAdapter.PlaylistViewHolder>(), Filterable{

    private val playlistWithVideoInfoList = mutableListOf<PlaylistWithVideoInfo>()
    private val filteredPlaylistWithVideoInfoList = mutableListOf<PlaylistWithVideoInfo>()
    var itemViewOnClick:(Long) -> Unit = {}
    var moreBtnOnClick:(PlaylistWithVideoInfo) -> Unit = {}

    fun updatePlaylists(playlistWithVideoInfo: List<PlaylistWithVideoInfo>) {
        filteredPlaylistWithVideoInfoList.clear()
        filteredPlaylistWithVideoInfoList.addAll(playlistWithVideoInfo)
        this.playlistWithVideoInfoList.clear()
        this.playlistWithVideoInfoList.addAll(playlistWithVideoInfo)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.view_playlist_item, parent, false)
        val binding = ViewPlaylistItemBinding.bind(view)
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlistWithVideoInfo = filteredPlaylistWithVideoInfoList[position]
        holder.bind(playlistWithVideoInfo)
    }

    override fun getItemCount() = filteredPlaylistWithVideoInfoList.size

    override fun getFilter(): Filter {
        return ItemFilter()
    }

    private inner class ItemFilter:Filter() {
        override fun performFiltering(constraint: CharSequence?) = FilterResults().apply {
            val constraintString = constraint.toString().lowercase()
            values = if(constraintString.isNullOrBlank()) {
                playlistWithVideoInfoList
            } else {
                val filteringPlaylistWithVideoInfo = mutableListOf<PlaylistWithVideoInfo>()
                playlistWithVideoInfoList.forEach { playlistWithVideoInfo ->
                    if(playlistWithVideoInfo.playlist.name.lowercase().contains(constraintString))
                        filteringPlaylistWithVideoInfo.add(playlistWithVideoInfo)
                }
                filteringPlaylistWithVideoInfo
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            filteredPlaylistWithVideoInfoList.clear()
            filteredPlaylistWithVideoInfoList.addAll(results?.values as MutableList<PlaylistWithVideoInfo>)
            notifyDataSetChanged()
        }
    }

    inner class PlaylistViewHolder(binding: ViewPlaylistItemBinding): RecyclerView.ViewHolder(binding.root) {
        private val name = binding.name
        private val moreBtn = binding.moreBtn

        fun bind(item:PlaylistWithVideoInfo) {
            name.text = item.playlist.name
            itemView.setOnClickListener {
                itemViewOnClick(item.playlist.playlistId)
            }
            // TODO: 플레이리스트 썸네일 코드 작성
            moreBtn.setOnClickListener { moreBtnOnClick(item) }
        }
    }
}