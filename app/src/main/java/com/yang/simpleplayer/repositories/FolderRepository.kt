package com.yang.simpleplayer.repositories

import com.yang.simpleplayer.data.VideoDao
import java.util.*

class FolderRepository(private val videoDao: VideoDao) {
    suspend fun getFolderNames():List<String> {
        val folderNames = TreeSet<String>()
        videoDao.getVideos().forEach { video ->
            folderNames.add(video.relativePath)
        }
        return folderNames.toList()
    }
}