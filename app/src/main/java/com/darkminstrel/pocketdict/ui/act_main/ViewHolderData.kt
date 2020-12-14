package com.darkminstrel.pocketdict.ui.act_main

import android.annotation.SuppressLint
import android.graphics.drawable.AnimationDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.darkminstrel.pocketdict.*
import com.darkminstrel.pocketdict.data.ParsedTranslation
import com.darkminstrel.pocketdict.data.ParsedTranslationItem
import com.darkminstrel.pocketdict.databinding.ActMainBinding
import com.darkminstrel.pocketdict.ui.views.ViewHolderTextPair
import com.darkminstrel.pocketdict.utils.convertHtml
import com.darkminstrel.pocketdict.utils.findCheckedChip
import com.darkminstrel.pocketdict.utils.setTintFromAttr
import com.google.android.material.chip.Chip

class ViewHolderData(private val binding: ActMainBinding, private val vm: ActMainVM) {
    private val inflater = LayoutInflater.from(binding.chipGroup.context)

    init {
        binding.chipGroup.setOnCheckedChangeListener { group, _ ->
            binding.containerTranslations.removeAllViews()
            val checkedChip:Chip? = group.findCheckedChip()
            val contexts = if(checkedChip!=null) (checkedChip.tag as ParsedTranslationItem).contexts
                else (group.tag as ParsedTranslation).defaultContexts
            contexts?.let{
                for(context in it){
                    inflater.inflate(R.layout.listitem_text_pair, binding.containerTranslations, false).also{view->
                        ViewHolderTextPair(view).setTexts(convertHtml(context.first), convertHtml(context.second))
                        binding.containerTranslations.addView(view)
                    }
                }
            }
        }
        binding.cbFavorite.setOnClickListener {
            parsed?.let{
                binding.cbFavorite.isEnabled = false
                vm.onChangeFavoriteStatus(it, !binding.cbFavorite.isChecked())
                binding.cbFavorite.toggle()
                binding.cbFavorite.beat()
            }
        }
        binding.btnSpeak.setOnClickListener {
            parsed?.let{
                val tts = vm.ttsManager
                if(tts.isMuted()){
                    Toast.makeText(binding.root.context, R.string.deviceIsMuted, Toast.LENGTH_SHORT).show()
                }else{
                    tts.speak(it.source, it.langFrom)
                }
            }
        }
    }

    private var parsed:ParsedTranslation?=null
    private var keys:List<String>?=null

    @SuppressLint("SetTextI18n")
    fun setData(parsed: ParsedTranslation){
        this.parsed = parsed
        updateFavoriteButton()

        binding.chipGroup.apply{
            removeAllViews()
            tag = parsed
            visibility = if(parsed.sortedItems.isEmpty()) View.GONE else View.VISIBLE
            parsed.sortedItems.forEach {
                val chip = inflater.inflate(R.layout.listitem_chip, this, false) as Chip
                chip.text = it.text
                chip.tag = it
                chip.isEnabled = !it.contexts.isNullOrEmpty()
                addView(chip)
            }
            clearCheck()
        }
        if(parsed.defaultContexts.isNullOrEmpty()) {
            binding.chipGroup.children.firstOrNull { it.isEnabled }?.let { (it as Chip).isChecked = true }
        }
    }

    fun setKeys(keys:List<String>){
        this.keys = keys
        updateFavoriteButton()
    }

    private fun updateFavoriteButton(){
        binding.cbFavorite.apply{
            isEnabled = keys!=null && parsed!=null
            setChecked(keys?.contains(parsed?.source) == true)
        }
    }

    fun setSpeechState(speechState: TextToSpeechManager.SpeechState) {
        val drawable = ContextCompat.getDrawable(binding.btnSpeak.context, when(speechState){
            TextToSpeechManager.SpeechState.UTTERING -> R.drawable.ic_volume_animated
            TextToSpeechManager.SpeechState.LOADING -> R.drawable.ic_volume_1_24px
            else -> R.drawable.ic_volume_2_24px
        })
        binding.btnSpeak.setImageDrawable(drawable)
        binding.btnSpeak.setTintFromAttr(if(speechState!=TextToSpeechManager.SpeechState.IDLE) R.attr.colorPrimary else R.attr.colorInactive)
        if(drawable is AnimationDrawable) drawable.start()
    }
}