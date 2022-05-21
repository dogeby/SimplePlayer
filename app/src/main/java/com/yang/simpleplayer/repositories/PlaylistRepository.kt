package com.yang.simpleplayer.repositories

import com.yang.simpleplayer.data.PlaylistDbDao
import com.yang.simpleplayer.data.VideoInfoDbDao
import com.yang.simpleplayer.models.Playlist
import com.yang.simpleplayer.models.PlaylistVideoInfoCrossRef
import com.yang.simpleplayer.models.VideoInfo

// TODO: 플레이리스트 안 동영상들 순서?
class PlaylistRepository(private val playlistDbDao: PlaylistDbDao, private val videoInfoDbDao: VideoInfoDbDao) {

    //ABORT
    fun insertPlaylist(playlist:Playlist) {
        playlistDbDao.insertPlaylist(playlist)
    }

    //IGNORE
    suspend fun addVideoInfoOnPlaylist(playlistVideoInfoCrossRef: PlaylistVideoInfoCrossRef) {
        if(videoInfoDbDao.getVideoInfo(playlistVideoInfoCrossRef.videoId) == null) {
            videoInfoDbDao.insertOrReplace(VideoInfo(playlistVideoInfoCrossRef.videoId))
        }
        playlistDbDao.insertPlaylistVideoInfoCrossRef(playlistVideoInfoCrossRef)
    }

    fun updatePlaylist(playlist:Playlist) = playlistDbDao.updatePlaylist(playlist)

    fun deletePlaylist(playlist: Playlist) {
        playlistDbDao.deletePlaylist(playlist)
        playlistDbDao.deletePlaylistVideoInfoCrossRef(playlist.playlistId)
    }

    fun removeVideoInfoFromPlaylist(playlistVideoInfoCrossRef: PlaylistVideoInfoCrossRef) {
        playlistDbDao.deletePlayListVideoInfoCrossRef(playlistVideoInfoCrossRef)
    }

    fun deleteAllPlaylist() {
        playlistDbDao.deleteAllPlaylistVideoInfoCrossRef()
        playlistDbDao.deleteAllPlaylist()
    }

    suspend fun getPlaylist(name:String) = playlistDbDao.getPlaylist(name)

    suspend fun getPlaylists() = playlistDbDao.getPlaylists()

    suspend fun getPlaylistsWithVideoInfo() = playlistDbDao.getPlaylistsWithVideoInfo()

    suspend fun getVideoInfoWithPlaylists() = playlistDbDao.getVideoInfoWithPlaylists()

    suspend fun getPlaylistWithVideoInfo(playlistId: Long) = playlistDbDao.getPlaylistWithVideoInfo(playlistId)
}