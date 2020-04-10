package com.darkminstrel.pocketdict.usecases

import com.darkminstrel.pocketdict.api.reverso.ApiReverso
import com.darkminstrel.pocketdict.api.reverso.RequestReverso
import com.darkminstrel.pocketdict.data.ParsedTranslation
import com.darkminstrel.pocketdict.data.ViewStateTranslate
import com.darkminstrel.pocketdict.database.Databaseable
import com.darkminstrel.pocketdict.safeRun
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UsecaseTranslate(private val api: ApiReverso, private val db: Databaseable) {

    fun getFavoriteKeys() = db.getAllKeys()

    suspend fun getTranslation(query: String): ViewStateTranslate = withContext(Dispatchers.IO){
        val favorite = db.get(query)
        if(favorite!=null){
            ViewStateTranslate.Data(favorite)
        }else{
            val apiRequest = RequestReverso(uiLang = "en", direction = "en-ru", source = query)
            val apiResponse = safeRun { api.getTranslation(apiRequest) }
            ViewStateTranslate.from(apiResponse)
        }
    }

    suspend fun getFavorite(key:String):ParsedTranslation? = withContext(Dispatchers.IO){ db.get(key) }

    suspend fun setFavorite(translation:ParsedTranslation, isFavorite:Boolean) = withContext(Dispatchers.IO){
        if(isFavorite) db.put(translation.source, translation)
        else db.delete(translation.source)
    }

}