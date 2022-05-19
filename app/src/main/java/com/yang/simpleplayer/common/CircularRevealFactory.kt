package com.yang.simpleplayer.common

import android.animation.Animator
import android.view.View
import android.view.ViewAnimationUtils
import androidx.core.animation.addListener
import com.yang.simpleplayer.R

object CircularRevealFactory {
    fun create(view:View?, centerView:View?, startRadius:Float, endRadius: Float, duration:Long): Animator? =
        ViewAnimationUtils.createCircularReveal(view, (centerView?.x!! + centerView.width/2).toInt(), (centerView.y+centerView.height/2).toInt(), startRadius, endRadius)
            .apply {
                view?.setBackgroundResource(R.color.translucent_25)
                this.duration = duration
                addListener()
            }
}