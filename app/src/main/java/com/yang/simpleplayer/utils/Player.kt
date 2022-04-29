package com.yang.simpleplayer.utils

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.yang.simpleplayer.models.Video

/**
 * Bug: 지원하지 않는 비디오 때문에 next 또는 prev 넘어가지 못하는 버그 존재
 */

class Player(private val player:ExoPlayer) {

    private fun addMediaItem(mediaItem: MediaItem) {
        player.addMediaItem(mediaItem)
    }
    private fun createMediaItem(uri: Uri) = MediaItem.fromUri(uri)
    fun setMediaItems(videos:List<Video>) {
        videos.forEach {addMediaItem(createMediaItem(it.contentUri))}
    }
    fun setCurrentVideo(index:Int) {
        player.seekTo(index, player.bufferedPosition)
    }
    fun attachStyledPlayerView(view: StyledPlayerView) {
        view.player = player
    }


    fun prepare() {player.prepare()}
    fun play() {player.play()}
    fun stop() {player.stop()}
    fun pause() {player.pause()}
    fun release() {player.release()}

    class Factory {
        fun build(context:Context) = Player(ExoPlayer.Builder(context).build())
    }
}