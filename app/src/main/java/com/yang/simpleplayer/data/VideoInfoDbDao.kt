package com.yang.simpleplayer.data

import androidx.room.*
import com.yang.simpleplayer.models.VideoInfo

@Dao
interface VideoInfoDbDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(videoInfo: VideoInfo)

    @Update
    fun update(videoInfo: VideoInfo)

    @Delete
    fun delete(videoInfo: VideoInfo)

    @Query("UPDATE VideoInfo SET playback_date = NULL WHERE video_id = :videoId")
    fun updatePlaybackDateNull(videoId:Long)

    @Query("SELECT video_id, playback_position_ms, playback_date FROM VideoInfo WHERE playback_date IS NOT NULL ORDER BY playback_date DESC LIMIT 50")
    suspend fun getRecentVideosInfo(): List<VideoInfo>

    @Query("SELECT video_id, playback_position_ms, playback_date FROM VideoInfo WHERE video_id IN (:videoIds)")
    suspend fun getVideosInfo(videoIds:LongArray):List<VideoInfo>

    @Query("SELECT video_id, playback_position_ms, playback_date FROM VideoInfo WHERE video_id = :videoId")
    suspend fun getVideoInfo(videoId:Long):VideoInfo?

    @Query("SELECT video_id, playback_position_ms, playback_date FROM VideoInfo")
    suspend fun getAllVideoInfo():List<VideoInfo>
}