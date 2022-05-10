package com.yang.simpleplayer.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class VideoInfo (
    @PrimaryKey @ColumnInfo(name = "id") val id:Long,
    @ColumnInfo(name = "playback_position_ms") val playbackPositionMs:Long = 0,
    @ColumnInfo(name = "playback_date") var playbackDate: Date? = null
)