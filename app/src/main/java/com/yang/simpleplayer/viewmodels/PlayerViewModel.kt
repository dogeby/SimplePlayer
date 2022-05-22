package com.yang.simpleplayer.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yang.simpleplayer.R
import com.yang.simpleplayer.models.PlayerPlaylist
import com.yang.simpleplayer.models.VideoInfo
import com.yang.simpleplayer.repositories.UserPreferencesRepository
import com.yang.simpleplayer.repositories.VideoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlayerViewModel (private val videoRepository: VideoRepository, private val userPreferencesRepository: UserPreferencesRepository):ViewModel() {
    val exceptionMessageResId = MutableLiveData<Int>()
    val playerPlaylist = MutableLiveData<PlayerPlaylist>()
    suspend fun getUserPreferences() = userPreferencesRepository.getFirstUserPreferences()

    fun requestPlayer (currentVideoId:Long, source:Any) {
        viewModelScope.launch(Dispatchers.IO) {
            val videos = videoRepository.getVideos(source as LongArray)
            if(videos.isEmpty()) exceptionMessageResId.postValue(R.string.empty_video_exception)
            else playerPlaylist.postValue(PlayerPlaylist(videos, videos.indexOfFirst { it.id == currentVideoId }))
        }
    }

    //중복 시 replace
    fun insertOrReplaceVideoInfo(videoInfo: VideoInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            videoRepository.insertOrReplace(videoInfo)
        }
    }

    class PlayerViewModelFactory(private val videoRepo: VideoRepository, private val userPreferencesRepo: UserPreferencesRepository):
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(PlayerViewModel::class.java)){
                return PlayerViewModel(videoRepo, userPreferencesRepo) as T
            }
            throw IllegalAccessException()
        }
    }
}