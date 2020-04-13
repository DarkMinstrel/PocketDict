package com.darkminstrel.pocketdict.data

import com.darkminstrel.pocketdict.api.leo.ResponseLeo
import com.darkminstrel.pocketdict.api.reverso.ResponseReverso

data class TranslationPair(
    val first:String,
    val second:String
)

data class ParsedTranslationItem(
    val text:String,
    val contexts:List<TranslationPair>?
)

data class ParsedTranslation(
    val source:String,
    val langFrom:String,
    val langTo:String,
    val defaultContexts:List<TranslationPair>?,
    val items:List<ParsedTranslationItem>
){

    fun getDescription():String {
        val sb = StringBuilder()
        for(item in items) {
            if(sb.isNotEmpty()) sb.append(", ")
            sb.append(item.text)
        }
        return sb.toString()
    }

    fun mergeWith(other:ParsedTranslation):ParsedTranslation{
        val source = this.source
        val langFrom = this.langFrom
        val langTo = this.langTo
        val defaultContexts = this.defaultContexts.orEmpty() + other.defaultContexts.orEmpty()
        val map:HashMap<String, ArrayList<TranslationPair>> = LinkedHashMap()
        (this.items + other.items).forEach {
            val pairs = map[it.text] ?: ArrayList()
            it.contexts?.let { contexts-> pairs.addAll(contexts) }
            map[it.text] = pairs
        }
        val items = map.keys.fold(ArrayList<ParsedTranslationItem>(), { list, key ->
            list.add(ParsedTranslationItem(key, map[key]))
            list
        })
        return ParsedTranslation(source, langFrom, langTo, defaultContexts, items)
    }
}

