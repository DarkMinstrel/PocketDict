package com.darkminstrel.pocketdict.utils

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.res.ColorStateList
import android.util.TypedValue
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.widget.ImageViewCompat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

fun ChipGroup.findCheckedChip(): Chip? {
    for(chip in children) if((chip as Chip).isChecked) return chip
    return null
}
@ColorInt
fun Context.getAttrColor(@AttrRes attrRes: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute (attrRes, typedValue, true)
    return typedValue.data
}
fun Context.getClipboardText():String? {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    return if(clipboard.hasPrimaryClip() && clipboard.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true){
        clipboard.primaryClip?.getItemAt(0)?.text?.toString()
    } else null
}
fun ImageView.setTint(@ColorRes colorRes: Int) {
    ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(ContextCompat.getColor(context, colorRes)))
}
fun ImageView.setTintFromAttr(@AttrRes attrRes: Int){
    ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(context.getAttrColor(attrRes)))
}
