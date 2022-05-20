package com.yang.simpleplayer.fragments.list.recent

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.yang.simpleplayer.R
import com.yang.simpleplayer.databinding.ViewRecentDateItemBinding
import com.yang.simpleplayer.databinding.ViewVideoItemBinding
import com.yang.simpleplayer.models.RecentVideoItem
import com.yang.simpleplayer.models.Video
import com.yang.simpleplayer.utils.Format
import com.yang.simpleplayer.utils.ImageLoader

class RecentVideoListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private val recentVideoItems = mutableListOf<RecentVideoItem>()
    private val filteredRecentVideoItems = mutableListOf<RecentVideoItem>()
    var itemViewOnclick = {_:Long, _:LongArray -> Unit}
    var moreBtnOnClick: (Video) -> Unit = {}

    fun updateVideos(recentVideoItems: List<RecentVideoItem>) {
        filteredRecentVideoItems.clear()
        filteredRecentVideoItems.addAll(recentVideoItems)
        this.recentVideoItems.clear()
        this.recentVideoItems.addAll(recentVideoItems)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return recentVideoItems[position].viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            RecentVideoItem.VIDEO -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.view_video_item, parent, false)
                val binding = ViewVideoItemBinding.bind(view)
                VideoViewHolder(binding)
            }
            RecentVideoItem.DATE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.view_recent_date_item, parent, false)
                val binding = ViewRecentDateItemBinding.bind(view)
                DateViewHolder(binding)
            }
            else -> throw RuntimeException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val recentVideoItem = recentVideoItems[position]
        when(recentVideoItem.viewType) {
            RecentVideoItem.VIDEO -> {
                (holder as VideoViewHolder).bind(recentVideoItem)
            }
            RecentVideoItem.DATE -> {
                (holder as DateViewHolder).bind(recentVideoItem)
            }
        }
    }

    override fun getItemCount() = filteredRecentVideoItems.size

    override fun getFilter(): Filter {
        return ItemFilter()
    }

    private inner class ItemFilter:Filter() {
        override fun performFiltering(constraint: CharSequence?) = FilterResults().apply {
            val constraintString = constraint.toString().lowercase()
            values = if(constraintString.isNullOrBlank()) {
                recentVideoItems
            }
            else {
                val filteringVideos = mutableListOf<Video>()
                recentVideoItems.forEach { recentVideoItem ->
                    if(recentVideoItem.video?.name?.lowercase()?.contains(constraintString) == true)
                        filteringVideos.add(recentVideoItem.video)
                }
                filteringVideos
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            filteredRecentVideoItems.clear()
            filteredRecentVideoItems.addAll(results?.values as MutableList<RecentVideoItem>)
            notifyDataSetChanged()
        }
    }

    inner class DateViewHolder(binding:ViewRecentDateItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val date = binding.date
        fun bind(recentVideoItem:RecentVideoItem) {
            date.text = recentVideoItem.date
        }
    }

    inner class VideoViewHolder(binding:ViewVideoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val thumbnail = binding.thumbnail
        private val name = binding.name
        private val duration = binding.duration
        private val moreBtn = binding.moreBtn

        fun bind(recentVideoItem: RecentVideoItem) {
            val video = recentVideoItem.video!!
            name.text = Format.splitExtension(video.name)
            ImageLoader.loadThumbnail(video.contentUri, thumbnail)
            duration.text = Format.msToHourMinSecond(video.duration)
            itemView.setOnClickListener {
                itemViewOnclick(video.id, longArrayOf(video.id))
            }
            moreBtn.setOnClickListener { moreBtnOnClick(video) }
        }
    }
}