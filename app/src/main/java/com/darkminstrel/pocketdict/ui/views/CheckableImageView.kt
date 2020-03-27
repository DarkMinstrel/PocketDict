package com.darkminstrel.pocketdict.ui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.Checkable

class CheckableImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
    ) : androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyleAttr), Checkable {

    init {
        setOnClickListener { toggle() }
    }
    private var mChecked = false
    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState: IntArray = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked) mergeDrawableStates(drawableState, intArrayOf(android.R.attr.state_checked))
        return drawableState
    }

    override fun setChecked(checked: Boolean) {
        if (mChecked != checked) {
            mChecked = checked
            refreshDrawableState()
        }
    }

    override fun isChecked(): Boolean {
        return mChecked
    }

    override fun toggle() {
        isChecked = !mChecked
    }
}