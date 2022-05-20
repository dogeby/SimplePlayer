package com.yang.simpleplayer.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yang.simpleplayer.models.PlaylistVideoInfoCrossRef
import com.yang.simpleplayer.models.Video
import com.yang.simpleplayer.repositories.PlaylistRepository
import com.yang.simpleplayer.repositories.VideoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * VideoListFragment의 ViewModel
 * 폴더에 있는 비디오들을 post한다
 * videoIds에 해당하는 비디오들을 post한다
 */

class VideoListViewModel(private val videoRepository: VideoRepository, private val playlistRepository: PlaylistRepository):ViewModel() {
    val progressVisible = MutableLiveData<Boolean>()
    val videos = MutableLiveData<List<Video>>()
    val exceptionMessageResId = MutableLiveData<String>()

    fun list(source:Long) { //source: playlist id
        progressVisible.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val playlistWithVideoInfo = playlistRepository.getPlaylistWithVideoInfo(source)
            val ids = LongArray(playlistWithVideoInfo.videoInfo.size){playlistWithVideoInfo.videoInfo[it].videoId}
            val videoList = videoRepository.getVideos(ids)
            videoList.forEach { video ->
                videoRepository.getVideoInfo(video.id)?.let {
                    video.videoInfo = it
                }
            }
            videos.postValue(videoList)
            progressVisible.postValue(false)
        }
    }

    fun list(source:String) {   //source: folder name
        progressVisible.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val videoList = videoRepository.getVideos(source)
            videoList.forEach { video ->
                videoRepository.getVideoInfo(video.id)?.let {
                    video.videoInfo = it
                }
            }
            videos.postValue(videoList)
            progressVisible.postValue(false)
        }
    }

    fun deleteVideoFromPlaylist(videoId:Long, playlistId:Long) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistRepository.removeVideoInfoFromPlaylist(PlaylistVideoInfoCrossRef(playlistId, videoId))
        }
    }

    class VideoListViewModelFactory(private val videoRepo:VideoRepository, private val playlistRepo:PlaylistRepository):
            ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(VideoListViewModel::class.java)) {
                return VideoListViewModel(videoRepo, playlistRepo) as T
            }
            throw IllegalAccessException()
        }
    }
}