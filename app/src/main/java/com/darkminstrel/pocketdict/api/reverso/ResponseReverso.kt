package com.darkminstrel.pocketdict.api.reverso

import com.darkminstrel.pocketdict.api.ResponseCommon
import com.darkminstrel.pocketdict.models.*

data class ResponseReversoContext(
    val source: String,
    val target: String,
    val isGood: Boolean,
) {
    fun mapToDomainModel() = TranslationPair(first = reformatHtml(source), second = reformatHtml(target))
}

data class ResponseReversoTranslation(
    val translation: String,
    val isRude: Boolean,
    val isSlang: Boolean,
    val isFromDict: Boolean,
    val contexts: List<ResponseReversoContext>?,
)

data class ResponseReversoSource(
    val source: String,
    val directionFrom: String,
    val directionTo: String,
    val translations: List<ResponseReversoTranslation>?,
)

data class ResponseReverso(
    val error: Boolean,
    val success: Boolean,
    val message: String?,
    val sources: List<ResponseReversoSource>?,
) : ResponseCommon {

    override fun mapToDomainModel(): Result<ParsedTranslation> {
        val source = sources?.firstOrNull()
        return if (error || !success || source == null) {
            if (error && message != null) Result.failure(ErrorTranslationServer(message))
            else Result.failure(ErrorTranslationEmpty)
        } else {
            val translations = source.translations?.ifEmpty { null }
            if (translations == null) Result.failure(ErrorTranslationEmpty)
            else {
                val defaultContexts = translations.firstOrNull { it.translation == "..." }?.contexts
                    ?.map { it.mapToDomainModel() }
                    ?: emptyList()
                val items = translations.filter { it.translation != "..." }.associateBy(
                    { it.translation }, { it.contexts?.map { it.mapToDomainModel() } ?: emptyList() }
                )
                Result.success(
                    ParsedTranslation(
                        source = source.source,
                        langFrom = source.directionFrom,
                        langTo = source.directionTo,
                        transcription = null,
                        defaultContexts = defaultContexts,
                        items = items
                    )
                )
            }
        }
    }
}

private fun reformatHtml(s: String): String = s.replace("<em>", "<b>").replace("</em>", "</b>")
