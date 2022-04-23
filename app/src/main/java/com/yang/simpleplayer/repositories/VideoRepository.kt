package com.yang.simpleplayer.repositories

import android.content.Context
import com.yang.simpleplayer.models.Video
import com.yang.simpleplayer.utils.VideoDao
open class VideoRepository {

    /**
     * 특정 비디오에 대한 함수
     */
    fun requestVideos(context: Context, videoIds:LongArray, completed: (Result<List<Video>>)->Unit) {
        VideoDao.getVideos(context, videoIds){ result -> completed(result) }
    }

    fun updateVideos(context: Context, videoIds:LongArray, completed: (Result<List<Video>>) -> Unit) {
        if(VideoDao.isSameVersion(context)) completed(Result.success(listOf()))
        else requestVideos(context, videoIds) { result -> completed(result) }
    }
    /**
     * 모든 비디오에 대한 함수
     */
    fun requestAllVideos(context: Context, completed: (Result<List<Video>>)->Unit) {
            VideoDao.getVideos(context){ result -> completed(result) }
    }

    fun updateAllVideos(context: Context, completed: (Result<List<Video>>) -> Unit) {
        if(VideoDao.isSameVersion(context)) completed(Result.success(listOf()))
        else requestAllVideos(context) { result -> completed(result) }
    }
}