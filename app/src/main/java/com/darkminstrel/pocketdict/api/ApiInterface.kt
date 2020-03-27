package com.darkminstrel.pocketdict.api

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {
    @Headers("Content-Type: application/json")
    @POST("/api2/TranslateSimple")
    suspend fun getTranslation(@Body request:RequestTranslate):ResponseTranslate
}