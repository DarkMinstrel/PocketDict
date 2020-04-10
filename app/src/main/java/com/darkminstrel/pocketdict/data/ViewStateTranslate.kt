package com.darkminstrel.pocketdict.data

import com.darkminstrel.pocketdict.ResultWrapper
import com.darkminstrel.pocketdict.api.reverso.ResponseReverso
import retrofit2.HttpException

sealed class ViewStateTranslate {
    object Progress: ViewStateTranslate()
    data class Data(val translation: ParsedTranslation): ViewStateTranslate()
    data class Error(val error:ErrorTranslation): ViewStateTranslate()

    companion object {
        fun from(apiResponse: ResultWrapper<ResponseReverso>): ViewStateTranslate {
            return when(apiResponse){
                is ResultWrapper.Success -> {
                    val response = apiResponse.value
                    if(response.error && response.message!=null) {
                        Error(ErrorTranslation.ErrorTranslationServer(response.message))
                    }
                    val parsed = ParsedTranslation.from(response)
                    parsed?.let { Data(it) } ?: Error(ErrorTranslation.ErrorTranslationEmpty)
                }
                is ResultWrapper.Error -> {
                    when(apiResponse.error){
                        is HttpException -> {
                            val code = apiResponse.error.code()
                            Error(ErrorTranslation.ErrorTranslationHttp(code))
                        }
                        else -> Error(ErrorTranslation.ErrorTranslationNetwork)
                    }
                }
            }
        }

    }
}