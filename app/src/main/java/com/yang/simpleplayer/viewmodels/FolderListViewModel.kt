package com.yang.simpleplayer.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yang.simpleplayer.repositories.FolderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FolderListViewModel(private val folderRepository: FolderRepository):ViewModel() {

    val progressVisible = MutableLiveData<Boolean>()
    val folderNames = MutableLiveData<List<String>>()
    val exceptionMessageResId = MutableLiveData<String>()

    fun list() {
        progressVisible.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            folderNames.postValue(folderRepository.getFolderNames())
            progressVisible.postValue(false)
        }
    }

    class FolderListViewModelFactory(private val folderRepository: FolderRepository):
            ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(FolderListViewModel::class.java)){
                return FolderListViewModel(folderRepository) as T
            }
            throw IllegalAccessException()
        }
    }
}