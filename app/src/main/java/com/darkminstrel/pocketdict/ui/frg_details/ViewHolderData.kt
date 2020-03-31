package com.darkminstrel.pocketdict.ui.frg_details

import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.AnimationDrawable
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.darkminstrel.pocketdict.R
import com.darkminstrel.pocketdict.convertHtml
import com.darkminstrel.pocketdict.data.ParsedTranslation
import com.darkminstrel.pocketdict.data.ParsedTranslationItem
import com.darkminstrel.pocketdict.findCheckedChip
import com.darkminstrel.pocketdict.setTintFromAttr
import com.darkminstrel.pocketdict.ui.BeatInterpolator
import com.darkminstrel.pocketdict.ui.views.ViewHolderTextPair
import com.darkminstrel.pocketdict.ui.views.CheckableImageView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class ViewHolderData(rootView:View, private val vm: FrgDetailsViewModel) {
    private val tvWordSource = rootView.findViewById<TextView>(R.id.tvWordSource)
    private val chipGroup = rootView.findViewById<ChipGroup>(R.id.chipGroup)
    private val containerTranslations = rootView.findViewById<ViewGroup>(R.id.containerTranslations)
    private val cbFavorite = rootView.findViewById<CheckableImageView>(R.id.cbFavorite)
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
                vm.onChangeFavoriteStatus(it, !cbFavorite.isChecked)
                cbFavorite.toggle()

                cbFavorite.animate().cancel()
                cbFavorite.scaleX = 1.0f
                cbFavorite.scaleY = 1.0f
                cbFavorite.animate().scaleX(1.2f).scaleY(1.2f).setInterpolator(BeatInterpolator()).start()

                cbFavorite.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            }
        }
        btnSpeak.setOnClickListener {
            parsed?.let{
                val tts = vm.ttsManager
                if(tts.isMuted()){
                    Toast.makeText(rootView.context, R.string.deviceIsMuted, Toast.LENGTH_SHORT).show()
                }else{
                    tts.speak(it.source, "en")  //TODO
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
        chipGroup.visibility = if(parsed.items.isEmpty()) View.GONE else View.VISIBLE
        for(translation in parsed.items){
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
        cbFavorite.isEnabled = true
        cbFavorite.isChecked = keys?.contains(parsed?.source) == true
    }

    fun setUttering(isUttering: Boolean) {
        val drawable = btnSpeak.context.getDrawable(if(isUttering) R.drawable.ic_volume_animated else R.drawable.ic_volume_2_24px)
        btnSpeak.setImageDrawable(drawable)
        btnSpeak.setTintFromAttr(if(isUttering) R.attr.colorSecondary else android.R.attr.textColorPrimary)
        if(drawable is AnimationDrawable) drawable.start()
    }
}