package com.yang.simpleplayer.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yang.simpleplayer.models.VideoInfo
import com.yang.simpleplayer.repositories.VideoRepository
import com.yang.simpleplayer.utils.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerViewModel (private val repository: VideoRepository, private val player:Player):ViewModel() {

    val progressVisible = MutableLiveData<Boolean>()
    val exceptionMessageResId = MutableLiveData<String>()
    val isSetVideo = MutableLiveData<Boolean>()

    fun requestPlayer (currentVideoId:Long, source:Any) {
        progressVisible.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val videos = repository.getVideos(source as LongArray)
            player.apply {
                withContext(Dispatchers.Main) {
                    setMediaItems(videos)
                    setCurrentVideo(videos.indexOfFirst { it.id == currentVideoId })
                }
            }
            isSetVideo.postValue(true)
        }
        progressVisible.postValue(false)
    }

    //중복 시 replace
    fun insertOrReplaceVideoInfo(videoInfo: VideoInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertOrReplace(videoInfo)
        }
    }

    class PlayerViewModelFactory(private val videoRepo: VideoRepository, private val player:Player):
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(PlayerViewModel::class.java)){
                return PlayerViewModel(videoRepo, player) as T
            }
            throw IllegalAccessException()
        }
    }
}