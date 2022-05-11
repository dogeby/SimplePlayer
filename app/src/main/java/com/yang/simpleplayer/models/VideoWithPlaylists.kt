package com.yang.simpleplayer.models

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class VideoWithPlaylists(
    @Embedded val videoInfo: VideoInfo,
    @Relation(
        parentColumn = "video_id",
        entityColumn = "playlist_id",
        associateBy = Junction(PlaylistVideoInfoCrossRef::class)
    )
    val playlists:List<Playlist>
)
