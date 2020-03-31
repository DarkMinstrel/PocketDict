package com.darkminstrel.pocketdict.ui

import android.animation.TimeInterpolator
import android.view.animation.AccelerateDecelerateInterpolator

class BeatInterpolator: TimeInterpolator {

    private val accdec = AccelerateDecelerateInterpolator()

    override fun getInterpolation(time: Float): Float {
        val i = accdec.getInterpolation(time)
        return if(i<0.5f) i*2
        else (1-i)*2
    }

}