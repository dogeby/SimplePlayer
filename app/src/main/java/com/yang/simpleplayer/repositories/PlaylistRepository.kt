package com.yang.simpleplayer.repositories

import com.yang.simpleplayer.data.PlaylistDbDao
import com.yang.simpleplayer.data.VideoInfoDbDao
import com.yang.simpleplayer.models.Playlist
import com.yang.simpleplayer.models.PlaylistVideoInfoCrossRef
import com.yang.simpleplayer.models.VideoInfo

class PlaylistRepository(private val playlistDbDao: PlaylistDbDao, private val videoInfoDbDao: VideoInfoDbDao) {

    //ABORT
    suspend fun insertPlaylist(playlist:Playlist) {
        playlistDbDao.insertPlaylist(playlist)
    }

    //REPLACE
    suspend fun addVideoInfoOnPlaylist(playlistVideoInfoCrossRef: PlaylistVideoInfoCrossRef) {
        if(videoInfoDbDao.getVideoInfo(playlistVideoInfoCrossRef.videoId) == null) {
            videoInfoDbDao.insertOrReplace(VideoInfo(playlistVideoInfoCrossRef.videoId))
        }
        playlistDbDao.insertPlaylistVideoInfoCrossRef(playlistVideoInfoCrossRef)
    }

    suspend fun deletePlaylist(playlistId: Long) {
        playlistDbDao.deletePlaylistVideoInfoCrossRef(playlistId)
        playlistDbDao.deletePlaylist(playlistId)
    }

    suspend fun removeVideoInfoFromPlaylist(playlistId:Long, videoInfoId:Long) {
        playlistDbDao.deletePlayListVideoInfoCrossRef(playlistId, videoInfoId)
    }

    suspend fun getPlaylists() = playlistDbDao.getPlaylists()

    suspend fun getPlaylistsWithVideoInfo() = playlistDbDao.getPlaylistsWithVideoInfo()

    suspend fun getVideoInfoWithPlaylists() = playlistDbDao.getVideoInfoWithPlaylists()

    suspend fun getPlaylistWithVideoInfo(playlistId: Long) = playlistDbDao.getPlaylistWithVideoInfo(playlistId)
}