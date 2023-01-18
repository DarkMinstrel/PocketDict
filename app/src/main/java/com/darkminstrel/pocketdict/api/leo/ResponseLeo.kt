package com.darkminstrel.pocketdict.api.leo

import com.darkminstrel.pocketdict.api.ResponseCommon
import com.darkminstrel.pocketdict.models.ErrorTranslationEmpty
import com.darkminstrel.pocketdict.models.ErrorTranslationServer
import com.darkminstrel.pocketdict.models.ParsedTranslation

data class ResponseLeoTranslation(
    val value: String,
)

data class ResponseLeo(
    private val status: String,
    private val error_msg: String?,
    private val transcription: String?,
    private val word_value: String,
    private val translate: List<ResponseLeoTranslation>,
) : ResponseCommon {

    override fun mapToDomainModel(): Result<ParsedTranslation> {
        return if (status != "ok" || translate.isEmpty()) {
            if (!error_msg.isNullOrEmpty()) Result.failure(ErrorTranslationServer(error_msg))
            else Result.failure(ErrorTranslationEmpty)
        } else {
            if(translate.size == 1 && translate.first().value == word_value) {
                Result.failure(ErrorTranslationEmpty)
            }else {
                Result.success(
                    ParsedTranslation(
                        source = word_value,
                        langFrom = "en",
                        langTo = "ru",
                        transcription = transcription?.ifEmpty { null },
                        defaultContexts = emptyList(),
                        items = translate.associateBy({ it.value }, { emptyList() })
                    )
                )
            }
        }
    }
}
