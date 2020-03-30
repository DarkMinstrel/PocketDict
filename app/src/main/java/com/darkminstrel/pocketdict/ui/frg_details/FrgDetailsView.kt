package com.darkminstrel.pocketdict.ui.frg_details

import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import com.darkminstrel.pocketdict.DBG
import com.darkminstrel.pocketdict.R
import com.darkminstrel.pocketdict.data.ViewStateTranslate

class FrgDetailsView(rootView: View, vm:FrgDetailsViewModel) {
    private val scrollView = rootView.findViewById<ScrollView>(R.id.scrollView)
    private val progressBar = rootView.findViewById<View>(R.id.progressBar)
    private val tvError = rootView.findViewById<TextView>(R.id.tvError)
    private val vhData = ViewHolderData(rootView.findViewById(R.id.containerData), vm)

    fun setViewState(viewState: ViewStateTranslate) {
        DBG("View state: ${viewState.javaClass.simpleName}")
        progressBar.visibility = if(viewState is ViewStateTranslate.Progress) View.VISIBLE else View.INVISIBLE
        tvError.visibility = if(viewState is ViewStateTranslate.Error) View.VISIBLE else View.INVISIBLE
        scrollView.visibility = if(viewState is ViewStateTranslate.Data) View.VISIBLE else View.INVISIBLE

        if(viewState is ViewStateTranslate.Error){
            tvError.text = viewState.error.getMessage(tvError.context)
        }else if(viewState is ViewStateTranslate.Data){
            scrollView.scrollTo(0,0)
            vhData.setData(viewState.translation)
        }
    }

    fun setKeys(keys: List<String>) = vhData.setKeys(keys)

}