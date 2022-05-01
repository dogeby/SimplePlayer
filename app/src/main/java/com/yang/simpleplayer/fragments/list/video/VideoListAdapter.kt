package com.yang.simpleplayer.fragments.list.video

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.yang.simpleplayer.R
import com.yang.simpleplayer.databinding.ViewVideoItemBinding
import com.yang.simpleplayer.models.Video
import com.yang.simpleplayer.utils.Format
import com.yang.simpleplayer.utils.ImageLoader

class VideoListAdapter : RecyclerView.Adapter<VideoListAdapter.VideoViewHolder>(), Filterable {

    private val videos = mutableListOf<Video>()
    private val filteredVideos = mutableListOf<Video>()
    var itemViewOnclick = {_:Long, _:LongArray -> Unit}
    var moreBtnOnClick: (Video) -> Unit = {}

    fun updateVideos(videos: List<Video>) {
        filteredVideos.clear()
        filteredVideos.addAll(videos)
        this.videos.clear()
        this.videos.addAll(videos)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_video_item, parent, false)
        val binding = ViewVideoItemBinding.bind(view)
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = filteredVideos[position]
        holder.bind(video)
    }

    override fun getItemCount() = filteredVideos.size


    override fun getFilter(): Filter {
        return ItemFilter()
    }

    inner class ItemFilter:Filter() {
        override fun performFiltering(constraint: CharSequence?) = FilterResults().apply {
            val constraintString = constraint.toString().lowercase()
            values = if(constraintString.isNullOrBlank()) {
                videos
            }
            else {
                val filteringVideos = mutableListOf<Video>()
                videos.forEach { video ->
                    if(video.name.lowercase().contains(constraintString))
                        filteringVideos.add(video)
                }
                filteringVideos
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            filteredVideos.clear()
            filteredVideos.addAll(results?.values as MutableList<Video>)
            notifyDataSetChanged()
        }
    }

    inner class VideoViewHolder(binding:ViewVideoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val thumbnail = binding.thumbnail
        private val name = binding.name
        private val duration = binding.duration
        private val moreBtn = binding.moreBtn

        fun bind(video: Video) {
            name.text = Format.splitExtension(video.name)
            ImageLoader.loadThumbnail(video.contentUri, thumbnail)
            duration.text = Format.msToHourMinSecond(video.duration)
            itemView.setOnClickListener {
                val ids = mutableListOf<Long>()
                videos.forEach { ids.add(it.id) }
                itemViewOnclick(video.id, ids.toLongArray())
            }
            moreBtn.setOnClickListener { moreBtnOnClick(video) }
        }
    }
}