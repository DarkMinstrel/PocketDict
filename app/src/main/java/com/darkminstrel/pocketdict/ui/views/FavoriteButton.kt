package com.darkminstrel.pocketdict.ui.views

import android.animation.TimeInterpolator
import android.content.Context
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import com.darkminstrel.pocketdict.R

class FavoriteButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val iv: ImageView
    private var checked = false

    init{
        iv = inflate(context, R.layout.favorite_button, this).findViewById(android.R.id.icon)
    }

    fun setChecked(checked:Boolean){
        this.checked = checked
        iv.setImageResource(if(checked) R.drawable.ic_favorite_24px else R.drawable.ic_favorite_border_24px)
    }

    fun isChecked() = checked
    fun toggle() = setChecked(!checked)

    fun beat(){
        iv.animate().cancel()
        iv.scaleX = 1.0f
        iv.scaleY = 1.0f
        iv.animate().scaleX(1.2f).scaleY(1.2f).setInterpolator(BeatInterpolator()).start()
        iv.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }
    
    class BeatInterpolator: TimeInterpolator {
        private val accdec = AccelerateDecelerateInterpolator()
        override fun getInterpolation(time: Float): Float {
            val i = accdec.getInterpolation(time)
            return if(i<0.5f) i*2
            else (1-i)*2
        }
    }
}