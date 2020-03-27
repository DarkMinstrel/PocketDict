package com.darkminstrel.pocketdict.api

sealed class ApiResult<out T> {
    data class Success<out T>(val value: T): ApiResult<T>()
    data class Error(val error:Throwable): ApiResult<Nothing>()

    companion object {
        suspend fun <T> from(apiCall: suspend () -> T): ApiResult<T> {
            return try {
                Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                Error(throwable)
            }
        }
    }

}