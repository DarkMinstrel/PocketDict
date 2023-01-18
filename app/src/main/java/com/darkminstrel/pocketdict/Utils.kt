package com.darkminstrel.pocketdict

import android.content.Context
import android.util.Log
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.darkminstrel.pocketdict.models.*
import retrofit2.HttpException

fun DBG(s: Any?) {
    Log.d("DICTDBG", s.toString())
}

fun Throwable.getMessage(context: Context): String = when (this) {
    is HttpException -> context.getString(R.string.errorHttp, this.code())
    is ErrorTranslation -> when (this) {
        is ErrorTranslationHttp -> String.format(context.getString(R.string.errorHttp), httpCode)
        is ErrorTranslationServer -> serverMessage
        ErrorTranslationEmpty -> context.getString(R.string.errorEmpty)
        else -> context.getString(R.string.errorNetwork)
    }
    else -> context.getString(R.string.errorUnknown)
}

fun String.toTextFieldValueSelected() = TextFieldValue(
    text = this,
    selection = TextRange(0, length)
)