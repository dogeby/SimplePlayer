package com.yang.simpleplayer.repositories

import android.content.Context
import com.yang.simpleplayer.models.Video
import com.yang.simpleplayer.utils.VideoDao

open class VideoRepository {

    /**
     * 특정 비디오 요청
     */
    // TODO: videoId로 요청시 함수 작성
    fun requestVideos(context: Context, source:Any, completed: (Result<List<Video>>)->Unit) {
        if(source is String) VideoDao.requestVideos(context, source){ result -> completed(result) }
    }

    fun updateVideos(context: Context, source:Any, completed: (Result<List<Video>>) -> Unit) {
        if(VideoDao.isSameVersion(context)){
            completed(Result.failure(Exception()))
        } else {
            if(source is String) {
                requestVideos(context, source) { result -> completed(result) }
            }
        }
    }

    /**
     * 폴더 요청
     */
    fun requestFolders(context: Context, completed: (Result<List<String>>) -> Unit) {
        VideoDao.requestFolder(context) { result -> completed(result)}}
    fun updateFolders(context: Context, completed: (Result<List<String>>) -> Unit) {
        if(VideoDao.isSameVersion(context)) completed(Result.failure(Exception()))
        VideoDao.requestFolder(context) { result -> completed(result)}
    }
}