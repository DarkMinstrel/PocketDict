package com.darkminstrel.pocketdict.ui.act_main

import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import com.darkminstrel.pocketdict.R
import com.darkminstrel.pocketdict.TextToSpeechManager
import com.darkminstrel.pocketdict.data.ViewStateTranslate
import com.darkminstrel.pocketdict.utils.DBG

class ActMainViewDetails(rootView: View, vm: ActMainVM) {
    private val containerTranslation = rootView.findViewById<View>(R.id.containerTranslation)
    private val scrollView = rootView.findViewById<ScrollView>(R.id.scrollView)
    private val progressBar = rootView.findViewById<View>(R.id.progressBar)
    private val tvError = rootView.findViewById<TextView>(R.id.tvError)
    private val containerMenu = rootView.findViewById<View>(R.id.containerMenu)
    private val vhData = ViewHolderData(rootView, vm)

    fun setVisible(visible:Boolean){
        containerTranslation.visibility = if(visible) View.VISIBLE else View.GONE
        containerMenu.visibility = if(visible) View.VISIBLE else View.GONE
    }

    fun setViewState(viewState: ViewStateTranslate) {
        progressBar.visibility = if(viewState is ViewStateTranslate.Progress) View.VISIBLE else View.INVISIBLE
        tvError.visibility = if(viewState is ViewStateTranslate.Error) View.VISIBLE else View.INVISIBLE
        scrollView.visibility = if(viewState is ViewStateTranslate.Data) View.VISIBLE else View.INVISIBLE
        containerMenu.visibility = if(viewState is ViewStateTranslate.Data) View.VISIBLE else View.INVISIBLE

        if(viewState is ViewStateTranslate.Error){
            tvError.text = viewState.error.getMessage(tvError.context)
        }else if(viewState is ViewStateTranslate.Data){
            scrollView.scrollTo(0,0)
            vhData.setData(viewState.translation)
        }
    }

    fun setKeys(keys: List<String>) = vhData.setKeys(keys)

    fun setSpeechState(speechState: TextToSpeechManager.SpeechState) {
        vhData.setSpeechState(speechState)
    }

}