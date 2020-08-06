package com.darkminstrel.pocketdict.usecases

import com.darkminstrel.pocketdict.ResultWrapper
import com.darkminstrel.pocketdict.api.ResponseCommon
import com.darkminstrel.pocketdict.api.leo.ApiLeo
import com.darkminstrel.pocketdict.api.leo.RequestLeo
import com.darkminstrel.pocketdict.api.reverso.ApiReverso
import com.darkminstrel.pocketdict.api.reverso.RequestReverso
import com.darkminstrel.pocketdict.data.ParsedTranslation
import com.darkminstrel.pocketdict.data.ViewStateTranslate
import com.darkminstrel.pocketdict.database.Databaseable
import com.darkminstrel.pocketdict.safeRun
import kotlinx.coroutines.*

private const val langFrom = "en"
private const val langTo = "ru"

class UsecaseTranslate(private val apiReverso: ApiReverso, private val apiLeo: ApiLeo, private val db: Databaseable) {

    val liveDataFavoriteKeys = db.getAllKeys()

    suspend fun getTranslation(query: String): ViewStateTranslate = withContext(Dispatchers.IO){
        val favorite = db.get(query)
        if(favorite!=null){
            ViewStateTranslate.Data(favorite)
        }else{
            val deferreds = ArrayList<Deferred<ResultWrapper<ResponseCommon>>>(2)
            deferreds += async(Dispatchers.IO) {
                val requestReverso = RequestReverso(uiLang = "en", direction = "$langFrom-$langTo", source = query)
                safeRun { apiReverso.getTranslation(requestReverso) }
            }
            if(langFrom=="en" && langTo=="ru") {
                deferreds += async(Dispatchers.IO) {
                    val requestLeo = RequestLeo(word = query)
                    safeRun { apiLeo.getTranslation(requestLeo) }
                }
            }
            val result = deferreds.awaitAll()
                .map { ViewStateTranslate.from(it) }
                .reduce { first,second -> first.mergeWith(second) }
            result
        }
    }

    suspend fun getFavorite(key:String):ParsedTranslation? = withContext(Dispatchers.IO){ db.get(key) }

    suspend fun setFavorite(translation:ParsedTranslation, isFavorite:Boolean) = withContext(Dispatchers.IO){
        if(isFavorite) db.put(translation.source, translation)
        else db.delete(translation.source)
    }

}