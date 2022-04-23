package com.yang.simpleplayer.fragments.list.video

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yang.simpleplayer.R
import com.yang.simpleplayer.databinding.ViewVideoItemBinding
import com.yang.simpleplayer.models.Video
import com.yang.simpleplayer.utils.Format
import com.yang.simpleplayer.utils.ImageLoader

class VideoListAdapter : RecyclerView.Adapter<VideoListAdapter.VideoViewHolder>() {

    private val videos = mutableListOf<Video>()
    var itemViewOnclick: (Video) -> Unit = {}
    var moreBtnOnClick: (Video) -> Unit = {}

    fun updateVideos(videos: List<Video>) {
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
        val video = videos[position]
        holder.bind(video)
    }

    override fun getItemCount() = videos.size

    inner class VideoViewHolder(binding:ViewVideoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val thumbnail = binding.thumbnail
        private val name = binding.name
        private val duration = binding.duration
        private val moreBtn = binding.moreBtn

        fun bind(item: Video) {
            Log.d("video", "${item.name} ${item.duration}")
            name.text = item.name
            ImageLoader.loadThumbnail(thumbnail.context, item) { bitmap ->
                thumbnail.setImageBitmap(bitmap)
            }
            duration.text = Format.msToHourMinSecond(item.duration.toLong())
            itemView.setOnClickListener { itemViewOnclick(item) }
            moreBtn.setOnClickListener { moreBtnOnClick(item) }
        }
    }
}