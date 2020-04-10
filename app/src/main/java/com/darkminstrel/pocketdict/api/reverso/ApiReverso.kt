package com.darkminstrel.pocketdict.api.reverso

import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

private const val BASE_URL = "https://cps.reverso.net/"

interface ApiReverso {
    companion object {
        fun build(moshi: Moshi): ApiReverso {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build().create(ApiReverso::class.java)
        }
    }

    @Headers("Content-Type: application/json")
    @POST("/api2/TranslateSimple")
    suspend fun getTranslation(@Body request: RequestReverso): ResponseReverso
}