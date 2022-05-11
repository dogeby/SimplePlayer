package com.yang.simpleplayer.data

import androidx.room.*
import com.yang.simpleplayer.models.Playlist
import com.yang.simpleplayer.models.PlaylistVideoInfoCrossRef
import com.yang.simpleplayer.models.PlaylistWithVideoInfo
import com.yang.simpleplayer.models.VideoWithPlaylists

@Dao
interface PlaylistDbDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPlaylist(playlist:Playlist)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistVideoInfoCrossRef(playlistVideoInfoCrossRef: PlaylistVideoInfoCrossRef)

    @Query("DELETE FROM Playlist WHERE playlist_id = :playlistId")
    suspend fun deletePlaylist(playlistId:Long)

    @Query("DELETE FROM PlaylistVideoInfoCrossRef WHERE playlist_id = :playlistId")
    suspend fun deletePlaylistVideoInfoCrossRef(playlistId: Long)

    @Query("DELETE FROM PlaylistVideoInfoCrossRef WHERE video_id = :videoInfoId")
    suspend fun deletePlayListVideoInfoCrossRef(videoInfoId:Long)

    @Query("DELETE FROM PlaylistVideoInfoCrossRef WHERE playlist_id = :playlistId AND video_id = :videoInfoId")
    suspend fun deletePlayListVideoInfoCrossRef(playlistId: Long, videoInfoId:Long)

    @Query("SELECT * FROM Playlist")
    suspend fun getPlaylists():List<Playlist>

    @Query("SELECT * FROM PlaylistVideoInfoCrossRef")
    suspend fun getAllPlaylistVideoInfoCrossRef():List<PlaylistVideoInfoCrossRef>

    @Transaction
    @Query("SELECT * FROM Playlist WHERE playlist_id = :playlistId")
    suspend fun getPlaylistWithVideoInfo(playlistId: Long): PlaylistWithVideoInfo

    @Transaction
    @Query("SELECT * FROM Playlist")
    suspend fun getPlaylistsWithVideoInfo(): List<PlaylistWithVideoInfo>

    @Transaction
    @Query("SELECT * FROM VideoInfo")
    suspend fun getVideoInfoWithPlaylists():List<VideoWithPlaylists>
}