package com.yang.simpleplayer.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.yang.simpleplayer.models.Video
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ImageLoader {
    fun loadThumbnail(uri: Uri, imageView: ImageView) {
        Glide.with(imageView.context).asBitmap().load(uri).into(imageView)
    }
}