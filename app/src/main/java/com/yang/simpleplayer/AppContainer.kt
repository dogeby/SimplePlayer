package com.yang.simpleplayer

import android.content.Context
import com.yang.simpleplayer.data.AppDatabase
import com.yang.simpleplayer.data.VideoDao
import com.yang.simpleplayer.repositories.FolderRepository
import com.yang.simpleplayer.repositories.PlaylistRepository
import com.yang.simpleplayer.repositories.VideoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AppContainer(context:Context) {
    private val appDatabase = AppDatabase.getDatabase(context)
    private val videoDao = VideoDao(context)
    val videoRepository = VideoRepository(videoDao, appDatabase.videoInfoDbDao())
    val folderRepository = FolderRepository(videoDao)
    val playlistRepository = PlaylistRepository(appDatabase.playlistDbDao(), appDatabase.videoInfoDbDao())
    init {
        GlobalScope.launch(Dispatchers.IO) {
            checkInvalidData()
        }
    }

    private suspend fun checkInvalidData() {
        val playlistDbDao = appDatabase.playlistDbDao()
        val videos = videoDao.getVideos()
        val videoHashSet = HashSet<Long>()
        videos.forEach { videoHashSet.add(it.id) }
        //checkInvalidPlaylistVideoInfoCrossRef
        val playlistVideoInfoCrossRefs = playlistDbDao.getAllPlaylistVideoInfoCrossRef()
        playlistVideoInfoCrossRefs.forEach { playlistVideoInfoCrossRef ->
            if(!videoHashSet.contains(playlistVideoInfoCrossRef.videoId)) {
                playlistDbDao.deletePlayListVideoInfoCrossRef(playlistVideoInfoCrossRef.videoId)
            }
        }
        //checkInvalidVideoInfo
        val videosInfo = videoRepository.getAllVideoInfo()
        videosInfo.forEach { videoInfo ->
            if(!videoHashSet.contains(videoInfo.videoId)) {
                videoRepository.delete(videoInfo)
            }
        }
    }
}