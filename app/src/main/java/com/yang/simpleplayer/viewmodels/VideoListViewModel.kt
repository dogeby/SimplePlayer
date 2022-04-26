package com.yang.simpleplayer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yang.simpleplayer.models.Video
import com.yang.simpleplayer.repositories.VideoRepository

/**
 * VideoListFragment의 ViewModel
 * 폴더에 있는 비디오들을 post한다
 * videoIds에 해당하는 비디오들을 post한다
 */

class VideoListViewModel(private val repository: VideoRepository, application: Application): AndroidViewModel(application) {

    val progressVisible = MutableLiveData<Boolean>()
    val videos = MutableLiveData<List<Video>>()
    val exceptionMessageResId = MutableLiveData<String>()

    fun list(source:Any) {
        progressVisible.postValue(true)
        repository.requestVideos(getApplication(), source) { requestVideosResult ->
            requestVideosResult.onSuccess { videos.postValue(it) }
            requestVideosResult.onFailure { exceptionMessageResId.postValue(it.message) }
            progressVisible.postValue(false)
        }
    }

    fun update(source: Any) {
        repository.updateVideos(getApplication(), source) {
            progressVisible.postValue(true)
            repository.updateVideos(getApplication(), source){ result ->
                result.onSuccess { videos.postValue(it) }
                result.onFailure { if(!it.message.isNullOrBlank()) exceptionMessageResId.postValue(it.message) }
                progressVisible.postValue(false)
            }
        }
    }

    class VideoListViewModelFactory(private val videoRepo:VideoRepository, private val application: Application):
            ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(VideoListViewModel::class.java)) {
                return VideoListViewModel(videoRepo, application) as T
            }
            throw IllegalAccessException()
        }
    }
}