package com.yang.simpleplayer.utils

import android.content.Context
import android.util.Log
import com.yang.simpleplayer.models.Folder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


// TODO: 폴더 정보 불러오기
object FolderDao {
    //임시
    private lateinit var folders: HashMap<String, Folder>
    fun getFolders(context: Context, completed:(Result<HashMap<String, Folder>>)->Unit) {
        if(VideoDao.isSameVersion(context) && ::folders.isInitialized) {
            completed(Result.success(folders))
            return
        }

        VideoDao.getVideos(context) { result ->
            GlobalScope.launch(Dispatchers.Default) {
                val tmpFolders = HashMap<String, Folder>()
                result.onSuccess { videos ->
                    videos.forEach { video ->
                        if (!tmpFolders.containsKey(video.relativePath)) tmpFolders[video.relativePath] = Folder(getParentFolderName(video.relativePath))
                        tmpFolders[video.relativePath]?.videoFiles?.add(video)
                    }
                }
                folders = tmpFolders
                withContext(Dispatchers.Main) {
                    completed(Result.success(folders))
                }
            }
        }

    }

    private fun getParentFolderName(relativePath:String):String {
        val tokens = relativePath.split('/')
        return tokens[tokens.lastIndex-1]
    }
}