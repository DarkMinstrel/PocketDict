package com.darkminstrel.pocketdict.ui.act_main

import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import com.darkminstrel.pocketdict.R
import com.darkminstrel.pocketdict.TextToSpeechManager
import com.darkminstrel.pocketdict.data.ViewStateTranslate
import com.darkminstrel.pocketdict.databinding.ActMainBinding
import com.darkminstrel.pocketdict.utils.DBG

class ActMainViewDetails(private val binding: ActMainBinding, vm: ActMainVM) {
    private val vhData = ViewHolderData(binding, vm)

    fun setViewState(viewState: ViewStateTranslate?) {
        binding.apply {
            containerTranslation.visibility = if(viewState!=null) View.VISIBLE else View.INVISIBLE

            progressBar.visibility = if (viewState is ViewStateTranslate.Progress) View.VISIBLE else View.INVISIBLE
            tvError.visibility = if (viewState is ViewStateTranslate.Error) View.VISIBLE else View.INVISIBLE
            scrollView.visibility = if (viewState is ViewStateTranslate.Data) View.VISIBLE else View.INVISIBLE
            containerMenu.visibility = if (viewState is ViewStateTranslate.Data) View.VISIBLE else View.GONE

            if (viewState is ViewStateTranslate.Error) {
                tvError.text = viewState.error.getMessage(tvError.context)
            } else if (viewState is ViewStateTranslate.Data) {
                scrollView.scrollTo(0, 0)
                vhData.setData(viewState.translation)
            }
        }
    }

    fun setKeys(keys: List<String>) = vhData.setKeys(keys)

    fun setSpeechState(speechState: TextToSpeechManager.SpeechState) {
        vhData.setSpeechState(speechState)
    }

}