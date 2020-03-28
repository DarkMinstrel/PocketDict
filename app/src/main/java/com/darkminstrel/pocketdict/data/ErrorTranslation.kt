package com.darkminstrel.pocketdict.data

import android.content.Context
import com.darkminstrel.pocketdict.R

sealed class ErrorTranslation {
    class ErrorTranslationHttp(val httpCode:Int):ErrorTranslation()
    class ErrorTranslationServer(val message:String):ErrorTranslation()
    object ErrorTranslationNetwork : ErrorTranslation()
    object ErrorTranslationEmpty : ErrorTranslation()

    fun getMessage(context: Context):String {
        return when(this){
            is ErrorTranslationHttp -> String.format(context.getString(R.string.errorHttp), httpCode)
            is ErrorTranslationServer -> message
            is ErrorTranslationNetwork -> context.getString(R.string.errorNetwork)
            is ErrorTranslationEmpty -> context.getString(R.string.errorEmpty)
        }
    }
}