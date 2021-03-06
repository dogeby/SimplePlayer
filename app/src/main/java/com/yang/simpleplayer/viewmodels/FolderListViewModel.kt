package com.yang.simpleplayer.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yang.simpleplayer.models.Folder
import com.yang.simpleplayer.repositories.FolderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FolderListViewModel(private val folderRepository: FolderRepository):ViewModel() {
    val folders = MutableLiveData<List<Folder>>()
    val exceptionMessageResId = MutableLiveData<Int>()

    fun list() {
        viewModelScope.launch(Dispatchers.IO) {
            folders.postValue(folderRepository.getFolders())
        }
    }

    class FolderListViewModelFactory(private val folderRepository: FolderRepository):
            ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(FolderListViewModel::class.java)){
                return FolderListViewModel(folderRepository) as T
            }
            throw IllegalAccessException()
        }
    }
}