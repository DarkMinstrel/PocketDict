package com.darkminstrel.pocketdict

sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T): ResultWrapper<T>()
    data class Error(val error: Throwable): ResultWrapper<Nothing>()
}

suspend fun <T> safeRun(call: suspend () -> T): ResultWrapper<T> {
    return try {
        ResultWrapper.Success(call.invoke())
    } catch (throwable: Throwable) {
        ResultWrapper.Error(throwable)
    }
}