package com.darkminstrel.pocketdict.data

import com.darkminstrel.pocketdict.api.ResponseTranslate

data class TranslationPair(
    val first:String,
    val second:String
)

data class ParsedTranslationItem (
    val text:String,
    val contexts:List<TranslationPair>?
)

data class ParsedTranslation(
    val source:String,
    val defaultContexts:List<TranslationPair>?,
    val items:List<ParsedTranslationItem>
){
    companion object {
        fun from(response:ResponseTranslate):ParsedTranslation?{
            if(!response.success || response.sources.isNullOrEmpty()) return null
            val source = response.sources.first()
            if(source.translations.isNullOrEmpty()) return null
            var defaultContexts:List<TranslationPair>? = null
            val items:ArrayList<ParsedTranslationItem> = ArrayList()
            for(translation in source.translations){
                var list = translation.contexts?.map { TranslationPair(reformatHtml(it.source), reformatHtml(it.target)) }
                list = if(list.isNullOrEmpty()) null else list
                if(translation.translation == "..."){
                    defaultContexts = list
                }else{
                    items.add(ParsedTranslationItem(translation.translation, list))
                }
            }
            return ParsedTranslation(source.source, defaultContexts, items)
        }

        private fun reformatHtml(s:String):String = s.replace("<em>","<b>").replace("</em>","</b>")
    }

    fun getDescription():String {
        val sb = StringBuilder()
        for(item in items) {
            if(sb.isNotEmpty()) sb.append(", ")
            sb.append(item.text)
        }
        return sb.toString()
    }
}

