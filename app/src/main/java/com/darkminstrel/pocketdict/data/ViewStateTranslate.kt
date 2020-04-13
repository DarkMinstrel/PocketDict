package com.darkminstrel.pocketdict.data

import com.darkminstrel.pocketdict.ResultWrapper
import com.darkminstrel.pocketdict.api.ResponseCommon
import retrofit2.HttpException

sealed class ViewStateTranslate {
    object Progress: ViewStateTranslate()
    data class Data(val translation: ParsedTranslation): ViewStateTranslate()
    data class Error(val error:ErrorTranslation): ViewStateTranslate()

    companion object {
        fun from(wrapper: ResultWrapper<ResponseCommon>): ViewStateTranslate {
            return when(wrapper){
                is ResultWrapper.Success -> {
                    val responseValue = wrapper.value
                    val errorMessage = responseValue.getErrorMessage()
                    if(errorMessage!=null) Error(ErrorTranslation.ErrorTranslationServer(errorMessage))
                    else{
                        val parsed = responseValue.toParsed()
                        if(parsed!=null) { Data(parsed) } else Error(ErrorTranslation.ErrorTranslationEmpty)
                    }
                }
                is ResultWrapper.Error -> {
                    when(wrapper.error){
                        is HttpException -> {
                            val code = wrapper.error.code()
                            Error(ErrorTranslation.ErrorTranslationHttp(code))
                        }
                        else -> Error(ErrorTranslation.ErrorTranslationNetwork)
                    }
                }
            }
        }
    }

    fun mergeWith(other:ViewStateTranslate):ViewStateTranslate{
        return when {
            this !is Data -> other
            other !is Data -> this
            else -> Data(this.translation.mergeWith(other.translation))
        }
    }
}