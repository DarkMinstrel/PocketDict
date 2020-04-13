package com.darkminstrel.pocketdict.usecases

import com.darkminstrel.pocketdict.api.leo.ApiLeo
import com.darkminstrel.pocketdict.api.leo.RequestLeo
import com.darkminstrel.pocketdict.api.reverso.ApiReverso
import com.darkminstrel.pocketdict.api.reverso.RequestReverso
import com.darkminstrel.pocketdict.data.ParsedTranslation
import com.darkminstrel.pocketdict.data.ViewStateTranslate
import com.darkminstrel.pocketdict.database.Databaseable
import com.darkminstrel.pocketdict.safeRun
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class UsecaseTranslate(private val apiReverso: ApiReverso, private val apiLeo: ApiLeo, private val db: Databaseable) {

    fun getFavoriteKeys() = db.getAllKeys()

    suspend fun getTranslation(query: String): ViewStateTranslate = withContext(Dispatchers.IO){
        val favorite = db.get(query)
        if(favorite!=null){
            ViewStateTranslate.Data(favorite)
        }else{
            val deferredReverso = async(Dispatchers.IO) {
                val requestReverso = RequestReverso(uiLang = "en", direction = "en-ru", source = query)
                safeRun { apiReverso.getTranslation(requestReverso) }
            }
            val deferredLeo = async(Dispatchers.IO) {
                val requestLeo = RequestLeo(word = query)
                safeRun { apiLeo.getTranslation(requestLeo) }
            }
            val result = listOf(deferredReverso, deferredLeo)
                .awaitAll()
                .map { ViewStateTranslate.from(it) }
                .reduce { first,second -> first.mergeWith(second)}
            result
        }
    }

    suspend fun getFavorite(key:String):ParsedTranslation? = withContext(Dispatchers.IO){ db.get(key) }

    suspend fun setFavorite(translation:ParsedTranslation, isFavorite:Boolean) = withContext(Dispatchers.IO){
        if(isFavorite) db.put(translation.source, translation)
        else db.delete(translation.source)
    }

}