package com.darkminstrel.pocketdict

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
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
import java.util.*

fun DBG(s:Any?){
    if(BuildConfig.DEBUG_FEATURES) Log.d("FLIODBG", s.toString())
}
fun DBGE(s:Throwable?){
    if(BuildConfig.DEBUG_FEATURES) Log.d("FLIODBG", "ERROR: "+s?.message)
}

fun ChipGroup.findCheckedChip():Chip? {
    for(chip in children) if((chip as Chip).isChecked) return chip
    return null
}
fun ImageView.setTint(@ColorRes colorRes: Int) {
    ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(ContextCompat.getColor(context, colorRes)))
}
fun ImageView.setTintFromAttr(@AttrRes attrRes: Int){
    ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(getAttrColor(context, attrRes)))
}

fun convertHtml(s:String):CharSequence{
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(s, Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(s)
    }
}

fun trimQuery(query:String) = query.trim().toLowerCase(Locale.getDefault())

@ColorInt
fun getAttrColor(context: Context, @AttrRes attrRes: Int): Int {
    val typedValue = TypedValue()
    context.theme.resolveAttribute (attrRes, typedValue, true)
    return typedValue.data
}

fun colorize(context:Context, text: String, constraintLower: String): CharSequence {
    if (text.isEmpty()) return text
    if (constraintLower.isEmpty()) return text
    val textLower = text.toLowerCase(Locale.US)
    if (textLower.length != text.length) return text //Izmir issue
    val highlightIndex = textLower.indexOf(constraintLower)
    if (highlightIndex == -1) return text
    val ssb = SpannableStringBuilder(text)
    val colorSpan = ForegroundColorSpan(getAttrColor(context, R.attr.colorPrimary))
    ssb.setSpan(colorSpan, highlightIndex, highlightIndex + constraintLower.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
    return ssb
}


fun getClipboardText(context:Context):String? {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    return if(clipboard.hasPrimaryClip() && clipboard.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true){
        clipboard.primaryClip?.getItemAt(0)?.text?.toString()
    }else null
}
