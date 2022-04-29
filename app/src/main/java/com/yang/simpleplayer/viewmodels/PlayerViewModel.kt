package com.yang.simpleplayer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yang.simpleplayer.repositories.VideoRepository
import com.yang.simpleplayer.utils.Player

class PlayerViewModel (private val repository: VideoRepository, application: Application): AndroidViewModel(application) {

    val progressVisible = MutableLiveData<Boolean>()
    val player = MutableLiveData<Player>()
    val exceptionMessageResId = MutableLiveData<String>()

    fun requestPlayer (currentVideoId:Long, source:Any) {
        progressVisible.postValue(true)
        repository.requestVideos(getApplication(), source){ result ->
            result.onSuccess { videos ->
                val player = Player.Factory().build(getApplication()).apply {
                    this.setMediaItems(videos)
                    this.setCurrentVideo(videos.indexOfFirst { it.id == currentVideoId })
                }
                this.player.postValue(player)
            }
            result.onFailure {
                exceptionMessageResId.postValue(it.message)
            }
            progressVisible.postValue(false)
        }
    }

    class PlayerViewModelFactory(private val videoRepo: VideoRepository, private val application: Application):
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(PlayerViewModel::class.java)){
                return PlayerViewModel(videoRepo, application) as T
            }
            throw IllegalAccessException()
        }
    }
}