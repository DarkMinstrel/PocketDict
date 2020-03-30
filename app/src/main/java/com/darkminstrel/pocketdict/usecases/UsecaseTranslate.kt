package com.darkminstrel.pocketdict.usecases

import com.darkminstrel.pocketdict.api.ApiInterface
import com.darkminstrel.pocketdict.api.ApiResult
import com.darkminstrel.pocketdict.api.RequestTranslate
import com.darkminstrel.pocketdict.data.ParsedTranslation
import com.darkminstrel.pocketdict.database.Databaseable
import com.darkminstrel.pocketdict.ui.ViewStateTranslate

class UsecaseTranslate(private val api: ApiInterface, private val db: Databaseable) {

    suspend fun getTranslation(query: String):ViewStateTranslate {
        val favorite = db.get(query)
        return if(favorite!=null){
            return ViewStateTranslate.Data(favorite)
        }else{
            val apiRequest = RequestTranslate(uiLang = "en", direction = "en-ru", source = query)
            val apiResult = ApiResult.from { api.getTranslation(apiRequest) }
            ViewStateTranslate.from(apiResult)
        }
    }

    suspend fun getFavorite(key:String):ParsedTranslation? = db.get(key)

    suspend fun getFavoriteKeys() = db.getAllKeys()

    suspend fun setFavorite(translation:ParsedTranslation, isFavorite:Boolean){
        if(isFavorite) db.put(translation.source, translation)
        else db.delete(translation.source)
    }

}