package com.darkminstrel.pocketdict.ui.views

import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView

class ViewHolderText(rootView: View):RecyclerView.ViewHolder(rootView) {
    private val tv = rootView.findViewById<TextView>(android.R.id.text1)

    fun setText(s:CharSequence){
        tv.text = s
    }

    fun setText(@StringRes stringRes:Int){
        tv.setText(stringRes)
    }


}