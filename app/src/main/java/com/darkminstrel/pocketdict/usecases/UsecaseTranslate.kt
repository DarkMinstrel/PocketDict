package com.darkminstrel.pocketdict.usecases

import android.util.LruCache
import com.darkminstrel.pocketdict.Config
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
import com.darkminstrel.pocketdict.utils.Debouncer
import kotlinx.coroutines.*

private const val langFrom = "en"
private const val langTo = "ru"

class UsecaseTranslate(private val apiReverso: ApiReverso, private val apiLeo: ApiLeo, private val db: Databaseable) {

    val liveDataFavoriteKeys = db.getAllKeys()
    private val memoryCache = LruCache<String, ParsedTranslation>(Config.MEMORY_CACHE_SIZE)

    suspend fun getFavorite(key:String):ParsedTranslation? {
        return memoryCache.get(key) ?: withContext(Dispatchers.IO){
            db.get(key)?.also { memoryCache.put(key, it) }
        }
    }

    suspend fun setFavorite(translation:ParsedTranslation, isFavorite:Boolean):Unit = withContext(Dispatchers.IO){
        translation.source.let{
            if(isFavorite) {
                db.put(it, translation)
                memoryCache.put(it, translation)
            }
            else db.delete(it)
        }
    }

    suspend fun getTranslation(query: String): ViewStateTranslate = getFavorite(query)?.let{ ViewStateTranslate.Data(it) } ?: getFromNetwork(query)

    private val debouncer = Debouncer(Config.NETWORK_DEBOUNCE)
    private suspend fun getFromNetwork(query: String):ViewStateTranslate = withContext(Dispatchers.IO){
        debouncer.debounce()
        ensureActive()

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
            .map { ViewStateTranslate.from(query, it) }
            .reduce { first,second -> first.mergeWith(second) }
        result
    }

}