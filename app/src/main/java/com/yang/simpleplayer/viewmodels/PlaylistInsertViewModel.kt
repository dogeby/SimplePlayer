package com.yang.simpleplayer.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yang.simpleplayer.models.Playlist
import com.yang.simpleplayer.repositories.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistInsertViewModel(private val playlistRepository: PlaylistRepository): ViewModel() {
    val isInsertSuccess = MutableLiveData<Boolean>()

    fun insertPlaylist(playlist:Playlist) {
        viewModelScope.launch(Dispatchers.IO){
            if(playlistRepository.getPlaylist(playlist.name) == null) {
                playlistRepository.insertPlaylist(playlist)
                isInsertSuccess.postValue(true)
            } else {
                isInsertSuccess.postValue(false)
            }
        }
    }

    class PlaylistInsertViewModelFactory(private val playlistRepository: PlaylistRepository):ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(PlaylistInsertViewModel::class.java)) {
                return PlaylistInsertViewModel(playlistRepository) as T
            }
            throw IllegalAccessException()
        }
    }
}