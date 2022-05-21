package com.yang.simpleplayer.utils

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.yang.simpleplayer.models.Video
import com.yang.simpleplayer.models.VideoInfo
import java.util.*

class Player(private val player:ExoPlayer) {
    private lateinit var videos:List<Video>
    var eventMediaItemTransitionCallback:(videoInfo:VideoInfo)->Unit = {}
    var eventVideoSizeChangedCallback:(width:Int, height:Int)->Unit = { _, _ ->  }

    init {
        player.addListener(object: com.google.android.exoplayer2.Player.Listener {
            override fun onEvents(
                player: com.google.android.exoplayer2.Player,
                events: com.google.android.exoplayer2.Player.Events
            ) {
                when {
                    events.contains(com.google.android.exoplayer2.Player.EVENT_VIDEO_SIZE_CHANGED) -> {
                        eventVideoSizeChangedCallback(player.videoSize.width, player.videoSize.height)
                    }
                    events.contains(com.google.android.exoplayer2.Player.EVENT_MEDIA_ITEM_TRANSITION) -> {
                        eventMediaItemTransitionCallback(videos[player.currentMediaItemIndex].videoInfo.apply {
                            this.playbackDate = Date(System.currentTimeMillis())
                        })
                    }
                }
            }
        })
    }

    private fun addMediaItem(mediaItem: MediaItem) {
        player.addMediaItem(mediaItem)
    }
    private fun createMediaItem(uri: Uri) = MediaItem.fromUri(uri)

    fun setMediaItems(videos:List<Video>, currentVideoIndex:Int) {
        this.videos = videos
        eventMediaItemTransitionCallback(videos[currentVideoIndex].videoInfo.apply {
            this.playbackDate = Date(System.currentTimeMillis())
        })
        videos.forEach {addMediaItem(createMediaItem(it.contentUri))}
        player.seekTo(currentVideoIndex, player.bufferedPosition)
    }

    fun attachStyledPlayerView(view: StyledPlayerView) {
        view.player = player
    }

    fun isPlaying() = player.isPlaying
    fun prepare() {player.prepare()}
    fun play() {player.play()}
    fun stop() {player.stop()}
    fun pause() {player.pause()}
    fun release() {player.release()}
    class Factory {
        fun build(context:Context) = Player(ExoPlayer.Builder(context).build())
    }
}