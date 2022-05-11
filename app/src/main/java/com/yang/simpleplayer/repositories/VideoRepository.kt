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

    suspend fun getVideos(ids:LongArray):List<Video> {
        return videoDao.getVideos().filter { video -> ids.contains(video.id) }
    }

    suspend fun getVideoInfo(id:Long) = videoInfoDbDao.getVideoInfo(id)

    suspend fun getVideosInfo(ids: LongArray) = videoInfoDbDao.getVideosInfo(ids)

    suspend fun getAllVideoInfo() = videoInfoDbDao.getAllVideoInfo()

    suspend fun getRecentVideosInfo() = videoInfoDbDao.getRecentVideosInfo()

    /**
     * 비디오 정보 DB
     */
    suspend fun insertOrReplace(videoInfo:VideoInfo) {
        videoInfoDbDao.insertOrReplace(videoInfo)
    }

    suspend fun update(videoInfo:VideoInfo) {
        videoInfoDbDao.update(videoInfo)
    }

    suspend fun delete(videoInfo:VideoInfo) {
        videoInfoDbDao.delete(videoInfo)
    }

    suspend fun checkInvalidVideoInfo() {
        val videosInfo = getAllVideoInfo()
        val videos = videoDao.getVideos()
        val videoHashSet = HashSet<Long>()
        videos.forEach { videoHashSet.add(it.id) }
        videosInfo.forEach { videoInfo ->
            if(!videoHashSet.contains(videoInfo.id)) {
                delete(videoInfo)
            }
        }
    }
}