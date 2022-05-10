package com.yang.simpleplayer

import android.content.Context
import com.yang.simpleplayer.data.VideoDao
import com.yang.simpleplayer.data.VideoInfoDatabase
import com.yang.simpleplayer.repositories.VideoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// TODO: checkInvalidVideoInfo 작동 체크 필요
class AppContainer(context:Context) {
    private val userVideoInfoDatabase = VideoInfoDatabase.getDatabase(context)
    private val videoDao = VideoDao(context)
    val videoRepository = VideoRepository(videoDao, userVideoInfoDatabase.videoDbDao())

    init {
        GlobalScope.launch(Dispatchers.IO) {
            videoRepository.checkInvalidVideoInfo()
        }
    }
}