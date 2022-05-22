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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * VideoListFragment의 ViewModel
 * 폴더에 있는 비디오들을 post한다
 * videoIds에 해당하는 비디오들을 post한다
 */

class VideoListViewModel(private val videoRepository: VideoRepository, private val playlistRepository: PlaylistRepository):ViewModel() {
    val videos = MutableLiveData<List<Video>>()
    val exceptionMessageResId = MutableLiveData<Int>()
    /** source: playlist id */
    fun list(source:Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val playlistWithVideoInfoFlow = playlistRepository.getPlaylistWithVideoInfo(source)
            playlistWithVideoInfoFlow.collect { playlistWithVideoInfo ->
                val ids = LongArray(playlistWithVideoInfo.videoInfo.size){playlistWithVideoInfo.videoInfo[it].videoId}
                val videoList = videoRepository.getVideos(ids)
                videoList.forEach { video ->
                    videoRepository.getVideoInfo(video.id)?.let {
                        video.videoInfo = it
                    }
                }
                videos.postValue(videoList)
            }
        }
    }
    /** source: folder name */
    fun list(source:String) {
        viewModelScope.launch(Dispatchers.IO) {
            val videoList = videoRepository.getVideos(source)
            videoList.forEach { video ->
                videoRepository.getVideoInfo(video.id)?.let {
                    video.videoInfo = it
                }
            }
            videos.postValue(videoList)
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