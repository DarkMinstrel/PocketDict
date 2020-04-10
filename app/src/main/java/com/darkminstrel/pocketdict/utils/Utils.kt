package com.darkminstrel.pocketdict.utils

import android.content.Context
import android.os.Build
import android.os.Looper
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import com.darkminstrel.pocketdict.BuildConfig
import com.darkminstrel.pocketdict.R
import java.util.*

fun DBG(s:Any?){
    if(BuildConfig.DEBUG_FEATURES) Log.d("FLIODBG", s.toString())
}
fun DBGE(s:Throwable?){
    DBG("ERROR: " + s?.message)
}
fun DBGT(s:Any?){
    DBG("$s is running on ${Thread.currentThread().name}")
}

fun assertWorkerThread(){
    if(Thread.currentThread() == Looper.getMainLooper().thread) throw RuntimeException("assertWorkerThread() failed")
}
fun assertUiThread(){
    if(Thread.currentThread() != Looper.getMainLooper().thread) throw RuntimeException("assertUiThread() failed")
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

fun colorize(context:Context, text: String, constraintLower: String): CharSequence {
    if (text.isEmpty()) return text
    if (constraintLower.isEmpty()) return text
    val textLower = text.toLowerCase(Locale.US)
    if (textLower.length != text.length) return text //Izmir issue
    val highlightIndex = textLower.indexOf(constraintLower)
    if (highlightIndex == -1) return text
    val ssb = SpannableStringBuilder(text)
    val colorSpan = ForegroundColorSpan(context.getAttrColor(R.attr.colorPrimary))
    ssb.setSpan(colorSpan, highlightIndex, highlightIndex + constraintLower.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
    return ssb
}

