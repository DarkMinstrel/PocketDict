package com.darkminstrel.pocketdict.data

import com.darkminstrel.pocketdict.utils.nullOrNotEmpty

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
    val transcription:String?,
    val defaultContexts:List<TranslationPair>?,
    private val items:List<ParsedTranslationItem>
){

    val sortedItems by lazy {
        items.sortedBy { if(it.contexts.isNullOrEmpty()) 1 else 0  }
    }

    fun getDescription():String {
        val sb = StringBuilder()
        for(item in sortedItems) {
            if(sb.isNotEmpty()) sb.append(", ")
            sb.append(item.text)
        }
        return sb.toString()
    }

    fun mergeWith(other:ParsedTranslation):ParsedTranslation{
        val source = this.source
        val langFrom = this.langFrom
        val langTo = this.langTo
        val transcription = this.transcription ?: other.transcription
        val defaultContexts = (this.defaultContexts.orEmpty() + other.defaultContexts.orEmpty()).nullOrNotEmpty()
        val map:HashMap<String, ArrayList<TranslationPair>> = LinkedHashMap()
        (this.items + other.items).forEach {
            val pairs = map[it.text] ?: ArrayList()
            it.contexts?.let { contexts-> pairs.addAll(contexts) }
            map[it.text] = pairs
        }
        val items = map.keys.fold(ArrayList<ParsedTranslationItem>(), { list, key ->
            list.apply { add(ParsedTranslationItem(key, map[key])) }
        })
        return ParsedTranslation(source, langFrom, langTo, transcription, defaultContexts, items)
    }
}

