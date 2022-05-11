package com.yang.simpleplayer.fragments.list

import com.yang.simpleplayer.models.Playlist
import com.yang.simpleplayer.models.PlaylistVideoInfoCrossRef

interface PlaylistContract {
    fun insertPlaylist(playlist: Playlist)
    fun addVideoInfoOnPlaylist(playlistVideoInfoCrossRef: PlaylistVideoInfoCrossRef)
    fun getPlaylists()
}