package com.yang.simpleplayer.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yang.simpleplayer.models.Playlist
import com.yang.simpleplayer.models.PlaylistVideoInfoCrossRef
import com.yang.simpleplayer.models.PlaylistWithVideoInfo
import com.yang.simpleplayer.repositories.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaylistViewModel(private val playlistRepository: PlaylistRepository): ViewModel() {
    val doListUpdate = MutableLiveData<Boolean>()
    val playlistsWithVideoInfo = MutableLiveData<List<PlaylistWithVideoInfo>>()

    fun list() {
        viewModelScope.launch(Dispatchers.IO) {
            val playlists = playlistRepository.getPlaylistsWithVideoInfo()
            playlistsWithVideoInfo.postValue(playlists)
        }
    }

    fun insertPlaylist(playlist:Playlist) {
        viewModelScope.launch(Dispatchers.IO){
            if(playlistRepository.getPlaylist(playlist.name) == null) {
                playlistRepository.insertPlaylist(playlist)
                doListUpdate.postValue(true)
            }
        }
    }

    fun addVideoInfoOnPlaylist(playlistVideoInfoCrossRef: PlaylistVideoInfoCrossRef) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistRepository.addVideoInfoOnPlaylist(playlistVideoInfoCrossRef)
        }
    }

    fun updatePlaylist(playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            if(playlistRepository.getPlaylist(playlist.name) == null) {
                playlistRepository.updatePlaylist(playlist)
                doListUpdate.postValue(true)
            }
        }
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistRepository.deletePlaylist(playlist)
        }
    }

    fun isSameNamePlaylist(playlist:Playlist, completed:(Boolean)->Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val isSameNamePlaylist = playlistRepository.getPlaylist(playlist.name)
            withContext(Dispatchers.Main) {
                completed(isSameNamePlaylist != null)
            }
        }
    }

    class PlaylistViewModelFactory(private val playlistRepository: PlaylistRepository):
            ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(PlaylistViewModel::class.java)) {
                return PlaylistViewModel(playlistRepository) as T
            }
            throw IllegalAccessException()
        }
    }
}