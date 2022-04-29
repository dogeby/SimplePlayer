package com.yang.simpleplayer.views

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import com.google.android.exoplayer2.ui.StyledPlayerView

class PlayerView(context:Context, attrs: AttributeSet? = null): StyledPlayerView(context, attrs) {
    private var isControllerVisible = false
    private val gestureDetector = GestureDetector(context, object:GestureDetector.OnGestureListener{
        override fun onDown(e: MotionEvent?) = true
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float) = true
        override fun onLongPress(e: MotionEvent?) {}
        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean = true
        override fun onShowPress(e: MotionEvent?) {}

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
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
    })
    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return true
    }

}