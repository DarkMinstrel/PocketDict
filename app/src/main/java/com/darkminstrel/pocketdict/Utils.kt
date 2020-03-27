package com.darkminstrel.pocketdict

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.text.Html
import android.util.Log
import androidx.core.view.children
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

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

fun convertHtml(s:String):CharSequence{
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(s, Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(s)
    }
}

fun getClipboardText(context:Context):String? {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    return if(clipboard.hasPrimaryClip() && clipboard.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true){
        clipboard.primaryClip?.getItemAt(0)?.text?.toString()
    }else null
}
