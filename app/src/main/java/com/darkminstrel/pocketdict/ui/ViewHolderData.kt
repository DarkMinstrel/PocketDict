package com.darkminstrel.pocketdict.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.darkminstrel.pocketdict.DBG
import com.darkminstrel.pocketdict.R
import com.darkminstrel.pocketdict.convertHtml
import com.darkminstrel.pocketdict.data.ParsedTranslation
import com.darkminstrel.pocketdict.data.ParsedTranslationItem
import com.darkminstrel.pocketdict.findCheckedChip
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class ViewHolderData(rootView:ViewGroup) {
    private val tvWordSource = rootView.findViewById<TextView>(R.id.tvWordSource)
    private val chipGroup = rootView.findViewById<ChipGroup>(R.id.chipGroup)
    private val containerTranslations = rootView.findViewById<ViewGroup>(R.id.containerTranslations)
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
    }

    fun setData(parsed: ParsedTranslation){
        tvWordSource.text = parsed.source

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
}