package com.yang.simpleplayer.utils

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.yang.simpleplayer.R
import com.yang.simpleplayer.models.Video
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


object VideoDao {
    // TODO: 비디오 정보 캐쉬만들기
    private lateinit var videos:MutableList<Video>
    private lateinit var version:String
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
        MediaStore.Video.Media.SIZE,
        MediaStore.Video.Media.RELATIVE_PATH
    )
    private const val NAME_ASC = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"

    /**
     * 비디오 리스트 불러오기
     */
    fun getVideos(context: Context, completed:(Result<List<Video>>) -> Unit) {
        val query = context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            NAME_ASC
        )

        // TODO: VideoDao - 비디오가 없는경우 문제가 발생할수있음?
        if(isSameVersion(context) && ::videos.isInitialized) {
            completed(Result.success(videos))
            return
        }

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val tmpVideos = mutableListOf<Video>()
                query?.use { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                    val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                    val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                    val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                    val relativePathColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.RELATIVE_PATH)
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val name = cursor.getString(nameColumn)
                        val duration = cursor.getInt(durationColumn)
                        val size = cursor.getInt(sizeColumn)
                        val contentUri: Uri = ContentUris.withAppendedId(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            id
                        )
                        val relativePath = cursor.getString(relativePathColumn)
                        tmpVideos += Video(id, contentUri, name, duration, size, relativePath)
                    }
                }
                videos = tmpVideos
                withContext(Dispatchers.Main) {
                    val result = if (videos.isEmpty()) Result.failure<List<Video>>(Exception(R.string.empty_video_exception.toString()))
                    else Result.success(videos.toList())
                    completed(result)
                }
            } catch (e:Exception) {
                withContext(Dispatchers.Main) {
                    completed(Result.failure(Exception(R.string.fail_load_video_exception.toString())))
                }
            }
        }
    }

    fun getVideos(context:Context, videoIds:LongArray, completed: (Result<List<Video>>)->Unit) {
        getVideos(context) { result ->
            result.onSuccess {
                GlobalScope.launch(Dispatchers.Default) {
                    val tmpVideos = mutableListOf<Video>()
                    videoIds.forEach { findVideo(it)?.let { it1 -> tmpVideos.add(it1) }}
                    if(videoIds.size != tmpVideos.size) completed(Result.failure(Exception(R.string.empty_video_exception.toString())))
                    else completed(Result.success(tmpVideos.toList()))
                }
            }
            result.onFailure { completed(result) }
        }
    }

    private fun findVideo(id:Long) = videos.find {it.id == id}

    fun isSameVersion(context:Context):Boolean {
        val currentVersion = MediaStore.getVersion(context)
        if(!::version.isInitialized) version = currentVersion
        return version == currentVersion
    }
}