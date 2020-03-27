package com.darkminstrel.pocketdict.ui

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ViewHolderTextPair(rootView: View):RecyclerView.ViewHolder(rootView) {
    private val tv1 = rootView.findViewById<TextView>(android.R.id.text1)
    private val tv2 = rootView.findViewById<TextView>(android.R.id.text2)

    fun setTexts(s1:CharSequence, s2:CharSequence){
        tv1.text = s1
        tv2.text = s2
    }

    fun animateAlpha(){
        tv2.alpha = 0f
        tv2.animate().cancel()
        tv2.animate().alpha(1f).start()
    }
}