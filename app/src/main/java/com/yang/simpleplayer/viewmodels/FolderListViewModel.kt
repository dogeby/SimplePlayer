package com.yang.simpleplayer.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yang.simpleplayer.repositories.VideoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FolderListViewModel(private val repository: VideoRepository):ViewModel() {

    val progressVisible = MutableLiveData<Boolean>()
    val folderNames = MutableLiveData<List<String>>()
    val exceptionMessageResId = MutableLiveData<String>()

    fun list() {
        progressVisible.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            folderNames.postValue(repository.getFolderNames())
            progressVisible.postValue(false)
        }
    }

    class FolderListViewModelFactory(private val videoRepo:VideoRepository):
            ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(FolderListViewModel::class.java)){
                return FolderListViewModel(videoRepo) as T
            }
            throw IllegalAccessException()
        }
    }
}