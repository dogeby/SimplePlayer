package com.yang.simpleplayer.repositories

import com.yang.simpleplayer.data.VideoDao
import com.yang.simpleplayer.models.Folder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class FolderRepository(private val videoDao: VideoDao) {
    suspend fun getFolders():List<Folder> {
        val foldersHashMap = HashMap<String, MutableList<Long>>()
        val folders = TreeSet<Folder>(compareBy { it.name })
        val videos = videoDao.getVideos()
        withContext(Dispatchers.Default) {
            videos.forEach { video ->
                if(!foldersHashMap.containsKey(video.relativePath)) {
                    foldersHashMap[video.relativePath] = mutableListOf()
                }
                foldersHashMap[video.relativePath]?.add(video.id)
            }
            foldersHashMap.forEach{entry: Map.Entry<String, MutableList<Long>> ->
                folders.add(Folder(entry.key, entry.value.toList()))
            }
        }
        return folders.toList()
    }
}