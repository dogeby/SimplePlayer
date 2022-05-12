package com.yang.simpleplayer.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["name"], unique = true)])
data class Playlist(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name ="playlist_id") val playlistId: Long,
    @ColumnInfo(name = "name") val name:String
) {
    constructor(name:String) : this(0, name)
}
