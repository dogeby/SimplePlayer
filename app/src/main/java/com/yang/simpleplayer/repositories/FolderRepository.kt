package com.yang.simpleplayer.repositories

import android.content.Context
import com.yang.simpleplayer.models.Folder
import com.yang.simpleplayer.utils.FolderDao
import com.yang.simpleplayer.utils.VideoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FolderRepository {

    fun requestFolders(context: Context, completed: (Result<List<Folder>>)->Unit) {
        FolderDao.getFolders(context) { result ->
            result.onSuccess { folderHashMap ->
                GlobalScope.launch(Dispatchers.Default) {
                    val tmpFolderList = folderHashMap.values.toMutableList()
                    tmpFolderList.sortBy { it.name.lowercase() }
                    withContext(Dispatchers.Main) { completed(Result.success(tmpFolderList.toList())) }
                }
            }
        }
    }

    fun updateFolders(context: Context, completed: (Result<List<Folder>>) -> Unit) {
        if(VideoDao.isSameVersion(context)) completed(Result.success(listOf()))
        else requestFolders(context) { result -> completed(result) }
    }
}