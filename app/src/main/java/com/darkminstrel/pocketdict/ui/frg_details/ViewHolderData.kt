package com.darkminstrel.pocketdict.ui.frg_details

import android.graphics.drawable.AnimationDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.darkminstrel.pocketdict.*
import com.darkminstrel.pocketdict.data.ParsedTranslation
import com.darkminstrel.pocketdict.data.ParsedTranslationItem
import com.darkminstrel.pocketdict.ui.views.ViewHolderTextPair
import com.darkminstrel.pocketdict.ui.views.FavoriteButton
import com.darkminstrel.pocketdict.utils.convertHtml
import com.darkminstrel.pocketdict.utils.findCheckedChip
import com.darkminstrel.pocketdict.utils.setTintFromAttr
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class ViewHolderData(rootView:View, private val vm: FrgDetailsViewModel) {
    private val tvWordSource = rootView.findViewById<TextView>(R.id.tvWordSource)
    private val chipGroup = rootView.findViewById<ChipGroup>(R.id.chipGroup)
    private val containerTranslations = rootView.findViewById<ViewGroup>(R.id.containerTranslations)
    private val cbFavorite = rootView.findViewById<FavoriteButton>(R.id.cbFavorite)
    private val btnSpeak = rootView.findViewById<ImageView>(R.id.btnSpeak)
    private val inflater = LayoutInflater.from(chipGroup.context)

    init {
        chipGroup.setOnCheckedChangeListener { group, _ ->
            containerTranslations.removeAllViews()
            val checkedChip:Chip? = group.findCheckedChip()
            val contexts = if(checkedChip!=null) (checkedChip.tag as ParsedTranslationItem).contexts
                else (chipGroup.tag as ParsedTranslation).defaultContexts
            contexts?.let{
                for(context in it){
                    inflater.inflate(R.layout.listitem_text_pair, containerTranslations, false).also{view->
                        ViewHolderTextPair(view).setTexts(convertHtml(context.first), convertHtml(context.second))
                        containerTranslations.addView(view)
                    }
                }
            }
        }
        cbFavorite.setOnClickListener {
            parsed?.let{
                cbFavorite.isEnabled = false
                vm.onChangeFavoriteStatus(it, !cbFavorite.isChecked())
                cbFavorite.toggle()
                cbFavorite.beat()
            }
        }
        btnSpeak.setOnClickListener {
            parsed?.let{
                val tts = vm.ttsManager
                if(tts.isMuted()){
                    Toast.makeText(rootView.context, R.string.deviceIsMuted, Toast.LENGTH_SHORT).show()
                }else{
                    tts.speak(it.source, it.langFrom)
                }
            }
        }
    }

    private var parsed:ParsedTranslation?=null
    private var keys:List<String>?=null

    fun setData(parsed: ParsedTranslation){
        this.parsed = parsed
        tvWordSource.text = parsed.source
        updateFavoriteButton()

        chipGroup.removeAllViews()
        chipGroup.tag = parsed
        chipGroup.visibility = if(parsed.sortedItems.isEmpty()) View.GONE else View.VISIBLE
        for(translation in parsed.sortedItems){
            val chip = inflater.inflate(R.layout.listitem_chip, chipGroup, false) as Chip
            chip.text = translation.text
            chip.tag = translation
            chip.isEnabled = !translation.contexts.isNullOrEmpty()
            chipGroup.addView(chip)
        }
        chipGroup.clearCheck()
    }

    fun setKeys(keys:List<String>){
        this.keys = keys
        updateFavoriteButton()
    }

    private fun updateFavoriteButton(){
        cbFavorite.isEnabled = keys!=null && parsed!=null
        cbFavorite.setChecked(keys?.contains(parsed?.source) == true)
    }

    fun setSpeechState(speechState: TextToSpeechManager.SpeechState) {
        val drawable = btnSpeak.context.getDrawable(when(speechState){
            TextToSpeechManager.SpeechState.UTTERING -> R.drawable.ic_volume_animated
            TextToSpeechManager.SpeechState.LOADING -> R.drawable.ic_volume_1_24px
            else -> R.drawable.ic_volume_2_24px
        })
        btnSpeak.setImageDrawable(drawable)
        btnSpeak.setTintFromAttr(if(speechState!=TextToSpeechManager.SpeechState.IDLE) R.attr.colorSecondary else android.R.attr.textColorPrimary)
        if(drawable is AnimationDrawable) drawable.start()
    }
}