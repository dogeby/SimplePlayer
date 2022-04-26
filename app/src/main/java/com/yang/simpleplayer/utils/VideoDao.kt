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
import java.util.*


object VideoDao {
    private val videos: HashMap<String, TreeSet<Video>> = hashMapOf()
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
        MediaStore.Video.Media.RELATIVE_PATH
    )
    private const val MEDIASTORE_NAME_ASC = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"
    private val VIDEO_NAME_COMPARABLE_ASC:Comparator<Video> = compareBy { it?.name?.lowercase() }
    private val VIDEO_NAME_COMPARABLE_DESC:Comparator<Video> = compareByDescending { it?.name?.lowercase() }
    private var videoNameComparator:Comparator<Video> = VIDEO_NAME_COMPARABLE_ASC


    /**
     * 비디오 리스트 불러오기
     */
    private fun updateVideos(context: Context, completed:(Result<HashMap<String, TreeSet<Video>>>) -> Unit) {
        if(isSameVersion(context)) {
            completed(Result.success(videos))
            return
        }
        val query = context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            MEDIASTORE_NAME_ASC
        )
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val tmpVideos = HashMap<String, TreeSet<Video>>()
                query?.use { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                    val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                    val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                    val relativePathColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.RELATIVE_PATH)
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val name = cursor.getString(nameColumn)
                        val duration = cursor.getInt(durationColumn)
                        val contentUri: Uri = ContentUris.withAppendedId(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            id
                        )
                        val relativePath = cursor.getString(relativePathColumn)
                        if (!tmpVideos.containsKey(relativePath)) tmpVideos[relativePath] = TreeSet(videoNameComparator)
                        tmpVideos[relativePath]?.add(Video(id, contentUri, name, duration, relativePath))
                    }
                }
                videos.clear()
                videos.putAll(tmpVideos)
                version = MediaStore.getVersion(context)
                withContext(Dispatchers.Main) {
                    val result = if (videos.isEmpty()) Result.failure(Exception(R.string.empty_video_exception.toString()))
                    else Result.success(videos)
                    completed(result)
                }
            } catch (e:Exception) {
                withContext(Dispatchers.Main) {
                    completed(Result.failure(Exception(R.string.fail_load_video_exception.toString())))
                }
            }
        }
    }

    fun requestVideos(context:Context, folderName:String, completed: (Result<List<Video>>)->Unit) {
        updateVideos(context) { result ->
            result.onSuccess { videos ->
                if(!videos[folderName].isNullOrEmpty()) completed(Result.success(videos[folderName]?.toList() as List<Video>))
            }
            result.onFailure { completed(Result.failure(it)) }
        }
    }

    fun requestVideos(context:Context, videoIds:LongArray, completed: (Result<List<Video>>) -> Unit) {
        updateVideos(context) { result ->
            result.onSuccess { videos ->
                val videoList = mutableListOf<Video>()
                videos.values.forEach { videoList.addAll(it) }
                Result.success(videoList.filter { videoIds.contains(it.id) })
            }
            result.onFailure { completed(Result.failure(it)) }
        }
    }

    fun requestFolders(context:Context, completed: (Result<List<String>>) -> Unit) {
        updateVideos(context) { result ->
            result.onSuccess { videos->
                completed(Result.success(videos.keys.toList()))
            }
            result.onFailure { completed(Result.failure(it)) }
        }
    }

    fun isSameVersion(context:Context):Boolean {
        if(!::version.isInitialized) { return false }
        return version ==  MediaStore.getVersion(context)
    }

    fun setVideoNameComparator(isAsc:Boolean) {
        videoNameComparator = if(isAsc) VIDEO_NAME_COMPARABLE_ASC else VIDEO_NAME_COMPARABLE_DESC
    }

}