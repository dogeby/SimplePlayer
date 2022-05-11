package com.yang.simpleplayer.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Playlist(
    @PrimaryKey @ColumnInfo(name = "video_id") val videoId:Long,
    @ColumnInfo(name = "name") var name:String
)
