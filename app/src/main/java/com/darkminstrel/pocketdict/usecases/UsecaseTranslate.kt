package com.darkminstrel.pocketdict.usecases

import com.darkminstrel.pocketdict.api.ApiInterface
import com.darkminstrel.pocketdict.api.RequestTranslate
import com.darkminstrel.pocketdict.api.ResponseTranslate

class UsecaseTranslate(private val api: ApiInterface) {

    suspend fun query(query: String):ResponseTranslate{
        //TODO add offline query

        val request = RequestTranslate(uiLang = "en", direction = "en-ru", source = query)
        return api.getTranslation(request)
    }
}