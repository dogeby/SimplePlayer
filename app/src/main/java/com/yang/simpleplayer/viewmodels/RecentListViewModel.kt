package com.yang.simpleplayer.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yang.simpleplayer.models.Video
import com.yang.simpleplayer.repositories.VideoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecentListViewModel(private val videoRepository: VideoRepository): ViewModel() {
    val progressVisible = MutableLiveData<Boolean>()
    val videos = MutableLiveData<List<Video>>()
    val exceptionMessageResId = MutableLiveData<String>()

    fun list() {
        progressVisible.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val recentVideosInfo = videoRepository.getRecentVideosInfo()
            val recentVideoIds = LongArray(recentVideosInfo.size){recentVideosInfo[it].videoId}
            val videoList = videoRepository.getVideos(recentVideoIds)
            withContext(Dispatchers.Default) {
                val videoMap = HashMap<Long, Video>()
                videoList.forEach { video ->
                    videoMap[video.id] = video
                }
                val resultVideoList = mutableListOf<Video>()
                recentVideosInfo.forEach { videoInfo ->
                    videoMap[videoInfo.videoId]?.let {
                        it.videoInfo = videoInfo
                        resultVideoList.add(it)
                    }
                }
                videos.postValue(resultVideoList)
                progressVisible.postValue(false)
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