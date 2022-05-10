package com.yang.simpleplayer.data

import androidx.room.*
import com.yang.simpleplayer.models.VideoInfo

@Dao
interface VideoInfoDbDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend  fun insertOrReplace(videoInfo: VideoInfo)

    @Update
    suspend fun update(videoInfo: VideoInfo)

    @Delete
    suspend fun delete(videoInfo: VideoInfo)

    @Query("SELECT id, playback_position_ms, playback_date FROM VideoInfo ORDER BY playback_date DESC LIMIT 50")
    fun getRecentVideosInfo(): List<VideoInfo>

    @Query("SELECT id, playback_position_ms, playback_date FROM VideoInfo WHERE id IN (:videoIds)")
    fun getVideosInfo(videoIds:LongArray):List<VideoInfo>

    @Query("SELECT id, playback_position_ms, playback_date FROM VideoInfo WHERE id = :videoId")
    fun getVideoInfo(videoId:Long):VideoInfo

    @Query("SELECT id, playback_position_ms, playback_date FROM VideoInfo")
    fun getAllVideoInfo():List<VideoInfo>
}