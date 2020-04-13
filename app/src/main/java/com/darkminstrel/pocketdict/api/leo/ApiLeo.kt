package com.darkminstrel.pocketdict.api.leo

import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

private const val BASE_URL = "https://api.lingualeo.com/"

interface ApiLeo {
    companion object {
        fun build(moshi: Moshi): ApiLeo {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build().create(ApiLeo::class.java)
        }
    }

    @Headers("Content-Type: application/json")
    @POST("/gettranslates")
    suspend fun getTranslation(@Body request: RequestLeo): ResponseLeo
}