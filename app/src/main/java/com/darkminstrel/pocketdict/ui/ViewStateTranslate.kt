package com.darkminstrel.pocketdict.ui

import com.darkminstrel.pocketdict.api.ApiResult
import com.darkminstrel.pocketdict.api.ResponseTranslate
import com.darkminstrel.pocketdict.data.ParsedTranslation
import retrofit2.HttpException

sealed class ViewStateTranslate {
    object Empty:ViewStateTranslate()
    object Progress:ViewStateTranslate()
    data class Data(val translation: ParsedTranslation):ViewStateTranslate()
    data class Error(val errorMessage:String):ViewStateTranslate()

    companion object {
        fun from(apiResult: ApiResult<ResponseTranslate>):ViewStateTranslate {
            return when(apiResult){
                is ApiResult.Success -> {
                    val response = apiResult.value
                    if(response.error && response.message!=null) {
                        Error(response.message) //internal error
                    }
                    val parsed = ParsedTranslation.from(response)
                    parsed?.let { Data(it)}
                        ?: Error("No translations found")  //TODO
                }
                is ApiResult.Error -> {
                    when(apiResult.error){
                        is HttpException -> {
                            val code = apiResult.error.code()
                            Error("HTTP error, code $code") //TODO
                        }
                        else -> Error("Network error") //TODO
                    }
                }
            }
        }

    }
}