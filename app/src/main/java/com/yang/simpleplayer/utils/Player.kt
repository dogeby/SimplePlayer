package com.yang.simpleplayer.utils

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.yang.simpleplayer.models.Video
import com.yang.simpleplayer.models.VideoInfo
import java.util.*

/**
 * Bug: 지원하지 않는 비디오 때문에 next 또는 prev 넘어가지 못하는 버그 존재
 * lateinit 주의
 */
// TODO:  mediaitemTransition event로 videoinfo를 처리했는데 mediaitem바뀌기전 처리해야하기 때문에 컨트롤뷰 커스텀해야할듯

class Player(private val player:ExoPlayer) {
    private lateinit var videos:List<Video>
    private var currentVideoIndex = 0

    private fun addMediaItem(mediaItem: MediaItem) {
        player.addMediaItem(mediaItem)
    }
    private fun createMediaItem(uri: Uri) = MediaItem.fromUri(uri)

    fun setMediaItems(videos:List<Video>) {
        this.videos = videos
        videos.forEach {addMediaItem(createMediaItem(it.contentUri))}
    }
    fun setCurrentVideo(index:Int) {
        currentVideoIndex = index
        player.seekTo(index, player.bufferedPosition)
    }
    fun attachStyledPlayerView(view: StyledPlayerView) {
        view.player = player
    }

    fun setMediaIndexTransitionCallback(callback:(videoInfo: VideoInfo) -> Unit) {
        player.addListener(object: com.google.android.exoplayer2.Player.Listener {
            override fun onEvents(
                player: com.google.android.exoplayer2.Player,
                events: com.google.android.exoplayer2.Player.Events
            ) {
                if(events.contains(com.google.android.exoplayer2.Player.EVENT_MEDIA_ITEM_TRANSITION)){
                    val currentVideoInfo = videos[currentVideoIndex].videoInfo
                    currentVideoInfo.playbackDate = Date(System.currentTimeMillis())
                    callback(currentVideoInfo)
                    currentVideoIndex = player.currentMediaItemIndex
                }
            }
        })
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