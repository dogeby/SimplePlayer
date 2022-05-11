package com.yang.simpleplayer.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Playlist(
    @PrimaryKey @ColumnInfo(name ="playlist_id") val playlistId: Long,
    @ColumnInfo(name = "name") val name:String
)
