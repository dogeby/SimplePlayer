package com.yang.simpleplayer.utils

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide

object ImageLoader {
    fun loadThumbnail(uri: Uri, imageView: ImageView) {
        Glide.with(imageView.context).asBitmap().load(uri).into(imageView)
    }
}