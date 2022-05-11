package com.yang.simpleplayer.data

import androidx.room.*
import com.yang.simpleplayer.models.VideoInfo

@Dao
interface VideoInfoDbDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend  fun insertOrReplace(videoInfo: VideoInfo)

    @Update
    suspend fun update(videoInfo: VideoInfo)
    /**
     * 최근 기록 삭제 같은 경우 playback_date를 NULL로 변경 필요
     */
    @Delete
    suspend fun delete(videoInfo: VideoInfo)

    @Query("SELECT video_id, playback_position_ms, playback_date FROM VideoInfo WHERE playback_date IS NOT NULL ORDER BY playback_date DESC LIMIT 50")
    suspend fun getRecentVideosInfo(): List<VideoInfo>

    @Query("SELECT video_id, playback_position_ms, playback_date FROM VideoInfo WHERE video_id IN (:videoIds)")
    suspend fun getVideosInfo(videoIds:LongArray):List<VideoInfo>

    @Query("SELECT video_id, playback_position_ms, playback_date FROM VideoInfo WHERE video_id = :videoId")
    suspend fun getVideoInfo(videoId:Long):VideoInfo?

    @Query("SELECT video_id, playback_position_ms, playback_date FROM VideoInfo")
    suspend fun getAllVideoInfo():List<VideoInfo>
}