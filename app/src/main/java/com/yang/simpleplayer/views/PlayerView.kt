package com.yang.simpleplayer.views

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import com.google.android.exoplayer2.ui.StyledPlayerView

class PlayerView(context:Context, attrs: AttributeSet? = null): StyledPlayerView(context, attrs) {
    private var isControllerVisible = false
    private var isDoubleTapOn = false
    private val rewindPositionMs = 5000L
    private val fastForwardPositionMs = 15000L
    private val gestureDetector = GestureDetector(context, object:
        GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            if(!useController || player == null) return false
            isControllerVisible = if(isControllerVisible){
                hideController()
                false
            } else{
                showController()
                true
            }
            return false
        }

        // TODO: 되감기, 빨리감기시 이미지 표시
        override fun onDoubleTap(e: MotionEvent?): Boolean {
            if(isDoubleTapOn) {
                val currentPosition = player?.currentPosition ?: 0L
                e?.x?.let {
                    if(it < this@PlayerView.width/2) {
                        player?.seekTo(currentPosition - rewindPositionMs)
                    } else {
                        player?.seekTo(currentPosition + fastForwardPositionMs)
                    }
                }
                return false
            }
            return super.onDoubleTap(e)
        }
    })

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return true
    }

    fun ableDoubleTabEvent() {
        isDoubleTapOn = true
    }
    fun disableDoubleTabEvent() {
        isDoubleTapOn = false
    }
}