package com.yang.simpleplayer.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yang.simpleplayer.R
import com.yang.simpleplayer.models.Video
import com.yang.simpleplayer.repositories.VideoRepository

/**
 * VideoListFragment의 ViewModel
 * videoIds의 id에 해당하는 비디오를 post한다
 */

class VideoListViewModel(private val repository: VideoRepository, private val videoIds:LongArray, application: Application): AndroidViewModel(application) {

    val progressVisible = MutableLiveData<Boolean>()
    val videos = MutableLiveData<List<Video>>()
    val exceptionMessage = MutableLiveData<String>()

    fun init() {
        progressVisible.postValue(true)
        repository.requestVideos(getApplication(), videoIds) { result ->
            result.onFailure {
                exceptionMessage.postValue(it.message)
            }
            result.onSuccess { videoList ->
                videos.postValue(videoList)
            }
            progressVisible.postValue(false)
        }
    }

    fun update() {
        repository.updateVideos(getApplication(), videoIds) {
            // TODO: 비디오리스트 update
        }
    }

    class VideoListViewModelFactory(private val videoRepo:VideoRepository, private val videoIds:LongArray, private val application: Application):
            ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(VideoListViewModel::class.java)) {
                return VideoListViewModel(videoRepo, videoIds, application) as T
            }
            throw IllegalAccessException(R.string.unkown_viewmodel_class_exception.toString())
        }
    }
}