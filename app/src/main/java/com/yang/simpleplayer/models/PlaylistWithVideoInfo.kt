package com.yang.simpleplayer.models

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PlaylistWithVideoInfo(
    @Embedded val playlist: Playlist,
    @Relation(
        parentColumn = "playlist_id",
        entityColumn = "video_id",
        associateBy = Junction(PlaylistVideoInfoCrossRef::class)
    )
    val videoInfo: List<VideoInfo>
)
