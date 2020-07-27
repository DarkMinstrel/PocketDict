package com.darkminstrel.pocketdict.api.leo

import com.darkminstrel.pocketdict.api.ResponseCommon
import com.darkminstrel.pocketdict.data.ParsedTranslation
import com.darkminstrel.pocketdict.data.ParsedTranslationItem

data class ResponseLeoTranslation(
    val value:String
)

data class ResponseLeo(
    private val status:String,
    private val error_msg:String?,
    private val transcription:String?,
    private val word_value:String,
    private val translate:List<ResponseLeoTranslation>
): ResponseCommon {

    override fun toParsed(): ParsedTranslation? {
        if(status!="ok" || translate.isEmpty()) return null
        val items = translate.map { ParsedTranslationItem(it.value, null) }
        return ParsedTranslation(word_value, "en", "ru", transcription, null, items)
    }

    override fun getErrorMessage(): String? = if(status!="ok" && !error_msg.isNullOrEmpty()) error_msg else null

}
