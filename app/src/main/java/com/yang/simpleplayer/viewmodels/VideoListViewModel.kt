package com.yang.simpleplayer.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yang.simpleplayer.models.Video
import com.yang.simpleplayer.repositories.VideoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * VideoListFragment의 ViewModel
 * 폴더에 있는 비디오들을 post한다
 * videoIds에 해당하는 비디오들을 post한다
 */

class VideoListViewModel(private val repository: VideoRepository):ViewModel() {
    val progressVisible = MutableLiveData<Boolean>()
    val videos = MutableLiveData<List<Video>>()
    val exceptionMessageResId = MutableLiveData<String>()

    fun list(source:Any) {
        progressVisible.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {

            val videoList = if(source is String) repository.getVideos(source)
                else repository.getVideos(source as LongArray)
            videoList.forEach { video ->
                repository.getVideoInfo(video.id)?.let {
                    video.videoInfo = it
                }
            }
            videos.postValue(videoList)
            progressVisible.postValue(false)
        }
    }

    class VideoListViewModelFactory(private val videoRepo:VideoRepository):
            ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(VideoListViewModel::class.java)) {
                return VideoListViewModel(videoRepo) as T
            }
            throw IllegalAccessException()
        }
    }
}