package com.yang.simpleplayer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yang.simpleplayer.R
import com.yang.simpleplayer.repositories.VideoRepository

class FolderListViewModel(private val repository: VideoRepository, application: Application): AndroidViewModel(application) {

    val progressVisible = MutableLiveData<Boolean>()
    val folderNames = MutableLiveData<List<String>>()
    val exceptionMessageResId = MutableLiveData<String>()

    fun list() {
        progressVisible.postValue(true)
        repository.requestFolders(getApplication()){ result ->
            result.onSuccess { folderNames ->
                this.folderNames.postValue(folderNames)
            }
            result.onFailure {
                exceptionMessageResId.postValue(it.message)
            }
            progressVisible.postValue(false)
        }
    }

    fun update() {
        progressVisible.postValue(true)
        repository.updateFolders(getApplication()){ result ->
            result.onSuccess { folderNames ->
                this.folderNames.postValue(folderNames)
            }
            result.onFailure {
                if(!it.message.isNullOrBlank()) exceptionMessageResId.postValue(it.message)
            }
            progressVisible.postValue(false)
        }
    }

    class FolderListViewModelFactory(private val videoRepo:VideoRepository, private val application: Application):
            ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(FolderListViewModel::class.java)){
                return FolderListViewModel(videoRepo, application) as T
            }
            throw IllegalAccessException(R.string.unkown_viewmodel_class_exception.toString())
        }
    }
}