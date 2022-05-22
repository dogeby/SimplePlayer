package com.yang.simpleplayer.repositories

import com.yang.simpleplayer.data.VideoDao
import com.yang.simpleplayer.data.VideoInfoDbDao
import com.yang.simpleplayer.models.Video
import com.yang.simpleplayer.models.VideoInfo

class VideoRepository(private val videoDao: VideoDao, private val videoInfoDbDao: VideoInfoDbDao) {

    /**
     * 비디오 요청
     */
    suspend fun getVideos(folderName:String):List<Video> {
        return videoDao.getVideos().filter { video -> video.relativePath == folderName }
    }

    suspend fun getVideos(ids: LongArray):List<Video> {
        return videoDao.getVideos().filter { video -> ids.contains(video.id) }
    }

    /**
     * 비디오 정보 DB
     */
    fun insertOrReplace(videoInfo:VideoInfo) {
        videoInfoDbDao.insertOrReplace(videoInfo)
    }

    fun update(videoInfo:VideoInfo) {
        videoInfoDbDao.update(videoInfo)
    }

    fun delete(videoInfo:VideoInfo) {
        videoInfoDbDao.delete(videoInfo)
    }

    fun updateAllVideoInfoPlaybackDateNull(){
        videoInfoDbDao.updateAllVideoInfoPlaybackDateNull()
    }

    suspend fun getVideoInfo(id:Long) = videoInfoDbDao.getVideoInfo(id)

    suspend fun getVideosInfo(ids: LongArray) = videoInfoDbDao.getVideosInfo(ids)

    suspend fun getAllVideoInfo() = videoInfoDbDao.getAllVideoInfo()

    fun getRecentVideosInfo() = videoInfoDbDao.getRecentVideosInfo()

    fun updatePlaybackDateNull(videoId: Long) = videoInfoDbDao.updatePlaybackDateNull(videoId)
}