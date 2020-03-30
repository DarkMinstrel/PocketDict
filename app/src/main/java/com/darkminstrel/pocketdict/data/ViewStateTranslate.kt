package com.darkminstrel.pocketdict.data

import com.darkminstrel.pocketdict.api.ApiResult
import com.darkminstrel.pocketdict.api.ResponseTranslate
import retrofit2.HttpException

sealed class ViewStateTranslate {
    object Progress: ViewStateTranslate()
    data class Data(val translation: ParsedTranslation): ViewStateTranslate()
    data class Error(val error:ErrorTranslation): ViewStateTranslate()

    companion object {
        fun from(apiResult: ApiResult<ResponseTranslate>): ViewStateTranslate {
            return when(apiResult){
                is ApiResult.Success -> {
                    val response = apiResult.value
                    if(response.error && response.message!=null) {
                        Error(ErrorTranslation.ErrorTranslationServer(response.message))
                    }
                    val parsed = ParsedTranslation.from(response)
                    parsed?.let { Data(it) }
                        ?: Error(ErrorTranslation.ErrorTranslationEmpty)
                }
                is ApiResult.Error -> {
                    when(apiResult.error){
                        is HttpException -> {
                            val code = apiResult.error.code()
                            Error(ErrorTranslation.ErrorTranslationHttp(code))
                        }
                        else -> Error(ErrorTranslation.ErrorTranslationNetwork)
                    }
                }
            }
        }

    }
}