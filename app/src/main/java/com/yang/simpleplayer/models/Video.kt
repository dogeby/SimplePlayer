package com.yang.simpleplayer.models

import android.net.Uri

data class Video(val id:Long, val contentUri: Uri, val name: String, val duration:Int, val size:Int, val relativePath:String)
