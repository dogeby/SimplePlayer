package com.yang.simpleplayer.models

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["playlist_id", "video_id"])
data class PlaylistVideoInfoCrossRef(
    @ColumnInfo(name = "playlist_id") val playlistId: Long,
    @ColumnInfo(name = "video_id") val videoId: Long
)
