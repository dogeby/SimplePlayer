package com.yang.simpleplayer.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yang.simpleplayer.fragments.list.PlaylistContract
import com.yang.simpleplayer.models.Playlist
import com.yang.simpleplayer.models.PlaylistVideoInfoCrossRef
import com.yang.simpleplayer.models.PlaylistWithVideoInfo
import com.yang.simpleplayer.repositories.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistViewModel(private val playlistRepository: PlaylistRepository): ViewModel(), PlaylistContract {
    val playlistsWithVideoInfo = MutableLiveData<List<PlaylistWithVideoInfo>>()
    val playlistList = MutableLiveData<List<Playlist>>()

    fun list() {
        viewModelScope.launch(Dispatchers.IO) {
            val playlists = playlistRepository.getPlaylistsWithVideoInfo()
            playlistsWithVideoInfo.postValue(playlists)
        }
    }

    override fun insertPlaylist(playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistRepository.insertPlaylist(playlist)
        }
    }

    override fun addVideoInfoOnPlaylist(playlistVideoInfoCrossRef: PlaylistVideoInfoCrossRef) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistRepository.addVideoInfoOnPlaylist(playlistVideoInfoCrossRef)
        }
    }

    override fun getPlaylists() {
        viewModelScope.launch(Dispatchers.IO) {
            val playlists = playlistRepository.getPlaylists()
            playlistList.postValue(playlists)
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistRepository.deletePlaylist(playlistId)
        }
    }

    class PlaylistViewModelFactory(private val playlistRepository: PlaylistRepository):
            ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(PlaylistViewModel::class.java)) {
                return PlaylistViewModel(playlistRepository) as T
            }
            throw IllegalAccessException()
        }
    }
}