package com.yang.simpleplayer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yang.simpleplayer.R
import com.yang.simpleplayer.models.Folder
import com.yang.simpleplayer.repositories.FolderRepository

class FolderListViewModel(private val repository: FolderRepository, application: Application): AndroidViewModel(application) {

    val progressVisible = MutableLiveData<Boolean>()
    val folders = MutableLiveData<List<Folder>>()
    val exceptionMessage = MutableLiveData<String>()

    fun init() {
        progressVisible.postValue(true)
        repository.requestFolders(getApplication()){ result ->
            result.onFailure {
                exceptionMessage.postValue(it.message)
            }
            result.onSuccess { folderList ->
                folders.postValue(folderList)
            }
            progressVisible.postValue(false)
        }
    }

    fun update() {
        repository.updateFolders(getApplication()){
            // TODO:  폴더리스트 update
        }
    }

    class FolderListViewModelFactory(private val folderRepo:FolderRepository, val application: Application):
            ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(FolderListViewModel::class.java)){
                return FolderListViewModel(folderRepo, application) as T
            }
            throw IllegalAccessException(R.string.unkown_viewmodel_class_exception.toString())
        }
    }
}