package com.yang.simpleplayer.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import androidx.annotation.RequiresApi
import com.yang.simpleplayer.models.Video
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ImageLoader {
    private val imageCache = mutableMapOf<String, Bitmap>()
    private val thumbnailSize = Size(640,480)
    fun loadThumbnail(context: Context, video: Video, completed:(Bitmap?)->Unit) {
        if(imageCache.containsKey(video.contentUri.toString())) {
            completed(imageCache[video.contentUri.toString()])
            return
        }
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val thumbnail = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    context.contentResolver.loadThumbnail(video.contentUri, thumbnailSize, null)
                } else {
                    MediaStore.Video.Thumbnails.getThumbnail(
                        context.contentResolver, video.id, MediaStore.Video.Thumbnails.MINI_KIND, BitmapFactory.Options() //MINI_KIND: 512 x 384
                    )
                }
                imageCache[video.contentUri.toString()] = thumbnail
                withContext(Dispatchers.Main) {
                    completed(thumbnail)
                }
            } catch (e:Exception) {
                withContext(Dispatchers.Main) {
                    completed(null)
                }
            }
        }
    }
}