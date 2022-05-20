package com.yang.simpleplayer.data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.yang.simpleplayer.models.Video

class VideoDao(private val context: Context) {
    private val collection =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }
    private val projection = arrayOf(
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.DISPLAY_NAME,
        MediaStore.Video.Media.DURATION,
        MediaStore.Video.Media.RELATIVE_PATH
    )
    private val MEDIASTORE_NAME_ASC = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"

    /**
     * 비디오 리스트 불러오기
     */
    suspend fun getVideos():List<Video> {
        val query = context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            MEDIASTORE_NAME_ASC
        )
        try {
            val videos = mutableListOf<Video>()
            query?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val relativePathColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.RELATIVE_PATH)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val duration = cursor.getInt(durationColumn)
                    val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    val relativePath = cursor.getString(relativePathColumn)
                    if(duration != 0) { //지원 안하는 동영상 방지용
                        videos.add(Video(id, contentUri, name, duration, relativePath))
                    }
                }
            }
            return videos
        } catch (e: Exception) {
            throw e
        }
    }
}