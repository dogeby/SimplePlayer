package com.yang.simpleplayer.views

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.yang.simpleplayer.R
import com.yang.simpleplayer.common.CircularRevealFactory

class PlayerView(context:Context, attrs: AttributeSet? = null): StyledPlayerView(context, attrs) {
    var duration = 2000L    //되감기, 빨리감기 animation duration
    var rewindPositionMs = 5000L
    var fastForwardPositionMs = 15000L
    private var isControllerVisible = false
    private var isDoubleTapOn = false
    private var rewindView:TextView? = null
    private var fastForwardView:TextView? = null
    private var touchViewContainer:View? = null   //되감기&빨리감기 시 백그라운드 반투명 색 애니메이션 할 view
    private val animationListener: Animation.AnimationListener = object:Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) { }
        override fun onAnimationEnd(animation: Animation?) { touchViewContainer?.setBackgroundResource(android.R.color.transparent) }
        override fun onAnimationRepeat(animation: Animation?) { }
    }
    private val animFadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out).apply {  //되감기 시 아이콘 fade out
        duration = this@PlayerView.duration
        setAnimationListener(animationListener)
    }
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
        override fun onDoubleTap(e: MotionEvent?): Boolean {
            if(isDoubleTapOn) {
                val currentPosition = player?.currentPosition ?: 0L
                e?.x?.let {
                    if(it < this@PlayerView.width/2) {
                        player?.seekTo(currentPosition - rewindPositionMs)
                        fastForwardView?.clearAnimation()
                        rewindView?.startAnimation(animFadeOut)
                        CircularRevealFactory.create(touchViewContainer, rewindView, rewindView?.width?.toFloat()!!, 0F, this@PlayerView.duration)?.start()
                    } else {
                        player?.seekTo(currentPosition + fastForwardPositionMs)
                        rewindView?.clearAnimation()
                        fastForwardView?.startAnimation(animFadeOut)
                        CircularRevealFactory.create(touchViewContainer, fastForwardView, fastForwardView?.width?.toFloat()!!, 0F, this@PlayerView.duration)?.start()
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

    fun ableDoubleTabEvent(rewindView:TextView, fastForwardView:TextView, touchViewContainer: View) {
        isDoubleTapOn = true
        rewindView.text = (rewindPositionMs/1000).toString()
        fastForwardView.text = (fastForwardPositionMs/1000).toString()
        this.rewindView = rewindView
        this.fastForwardView = fastForwardView
        this.touchViewContainer = touchViewContainer
    }
    fun disableDoubleTabEvent() {
        isDoubleTapOn = false
        this.rewindView = null
        this.fastForwardView = null
        this.touchViewContainer = null
    }
}