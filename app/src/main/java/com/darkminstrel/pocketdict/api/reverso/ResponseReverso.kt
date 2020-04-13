package com.darkminstrel.pocketdict.api.reverso

import com.darkminstrel.pocketdict.api.ResponseCommon
import com.darkminstrel.pocketdict.data.ParsedTranslation
import com.darkminstrel.pocketdict.data.ParsedTranslationItem
import com.darkminstrel.pocketdict.data.TranslationPair
import com.darkminstrel.pocketdict.utils.nullOrNotEmpty

data class ResponseReversoContext(
    val source:String,
    val target:String,
    val isGood:Boolean
)

data class ResponseReversoTranslation (
    val translation:String,
    val isRude:Boolean,
    val isSlang:Boolean,
    val isFromDict:Boolean,
    val contexts:List<ResponseReversoContext>?
)

data class ResponseReversoSource(
    val source:String,
    val directionFrom:String,
    val directionTo:String,
    val translations:List<ResponseReversoTranslation>?
)

data class ResponseReverso(
    val error:Boolean,
    val success:Boolean,
    val message:String?,
    val sources:List<ResponseReversoSource>?
): ResponseCommon {

    private fun reformatHtml(s:String):String = s.replace("<em>","<b>").replace("</em>","</b>")

    override fun toParsed(): ParsedTranslation? {
        if(!success || sources.isNullOrEmpty()) return null
        val source = sources.first()
        if(source.translations.isNullOrEmpty()) return null
        var defaultContexts:List<TranslationPair>? = null
        val items:ArrayList<ParsedTranslationItem> = ArrayList()
        for(translation in source.translations){
            var list = translation.contexts?.map { TranslationPair(reformatHtml(it.source), reformatHtml(it.target)) }
            list = list.nullOrNotEmpty()
            if(translation.translation == "..."){
                defaultContexts = list
            }else{
                items.add(ParsedTranslationItem(translation.translation, list))
            }
        }
        return ParsedTranslation(source.source, source.directionFrom, source.directionTo, null, defaultContexts, items)
    }

    override fun getErrorMessage(): String? = if(error && message!=null) message else null

}