package com.yang.simpleplayer.data

import androidx.room.*
import com.yang.simpleplayer.models.Playlist
import com.yang.simpleplayer.models.PlaylistVideoInfoCrossRef
import com.yang.simpleplayer.models.PlaylistWithVideoInfo
import com.yang.simpleplayer.models.VideoWithPlaylists

@Dao
interface PlaylistDbDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertPlaylist(playlist:Playlist)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPlaylistVideoInfoCrossRef(playlistVideoInfoCrossRef: PlaylistVideoInfoCrossRef)

    @Delete
    fun deletePlaylist(playlist: Playlist)

    @Delete
    fun deletePlayListVideoInfoCrossRef(playlistVideoInfoCrossRef: PlaylistVideoInfoCrossRef)

    @Query("DELETE FROM PlaylistVideoInfoCrossRef WHERE playlist_id = :playlistId")
    fun deletePlaylistVideoInfoCrossRef(playlistId: Long)

    @Query("DELETE FROM PlaylistVideoInfoCrossRef WHERE video_id = :videoInfoId")
    fun deletePlayListVideoInfoCrossRef(videoInfoId:Long)

    @Query("SELECT * FROM Playlist")
    suspend fun getPlaylists():List<Playlist>

    @Query("SELECT * FROM Playlist WHERE name = :name")
    suspend fun getPlaylist(name:String):Playlist

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