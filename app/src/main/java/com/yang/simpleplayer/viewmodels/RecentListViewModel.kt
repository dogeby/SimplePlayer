package com.yang.simpleplayer.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yang.simpleplayer.models.RecentVideoItem
import com.yang.simpleplayer.models.Video
import com.yang.simpleplayer.repositories.VideoRepository
import com.yang.simpleplayer.utils.Format
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecentListViewModel(private val videoRepository: VideoRepository): ViewModel() {
    val recentVideoItems = MutableLiveData<List<RecentVideoItem>>()
    val exceptionMessageResId = MutableLiveData<Int>()

    fun list() {
        viewModelScope.launch(Dispatchers.IO) {
            val recentVideoInfoFlow = videoRepository.getRecentVideosInfo()
            recentVideoInfoFlow.collect { recentVideoInfo ->
                val recentVideoIds = LongArray(recentVideoInfo.size){recentVideoInfo[it].videoId}
                val videoList = videoRepository.getVideos(recentVideoIds)
                withContext(Dispatchers.Default) {
                    val videoMap = HashMap<Long, Video>()
                    videoList.forEach { video ->
                        videoMap[video.id] = video
                    }
                    val recentVideoItemList = mutableListOf<RecentVideoItem>()
                    var korDate = if(recentVideoInfo.isNotEmpty()) {
                        val firstKorDate = recentVideoInfo[0].playbackDate?.let { Format.dateToKorDate(it) }
                        recentVideoItemList.add(RecentVideoItem(null, firstKorDate, RecentVideoItem.DATE))
                        firstKorDate
                    } else null
                    recentVideoInfo.forEach { videoInfo ->
                        videoMap[videoInfo.videoId]?.let {
                            it.videoInfo = videoInfo
                            val tmpKorDate = videoInfo.playbackDate?.let { date -> Format.dateToKorDate(date) }
                            if(tmpKorDate != korDate) {
                                korDate = tmpKorDate
                                recentVideoItemList.add(RecentVideoItem(null, korDate, RecentVideoItem.DATE))
                            }
                            recentVideoItemList.add(RecentVideoItem(it))
                        }
                    }
                    recentVideoItems.postValue(recentVideoItemList)
                }
            }
        }
    }

    fun deletePlaybackDate(videoId:Long) {
        viewModelScope.launch(Dispatchers.IO) {
            videoRepository.updatePlaybackDateNull(videoId)
        }
    }

    class RecentListViewModelFactory(private val videoRepository: VideoRepository):ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(RecentListViewModel::class.java)) {
                return RecentListViewModel(videoRepository) as T
            }
            throw IllegalAccessException()
        }
    }
}