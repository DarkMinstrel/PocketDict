package com.darkminstrel.pocketdict.usecases

import com.darkminstrel.pocketdict.History
import com.darkminstrel.pocketdict.HistoryRepository
import com.darkminstrel.pocketdict.api.ResponseCommon
import com.darkminstrel.pocketdict.api.leo.ApiLeo
import com.darkminstrel.pocketdict.api.leo.RequestLeo
import com.darkminstrel.pocketdict.api.reverso.ApiReverso
import com.darkminstrel.pocketdict.api.reverso.RequestReverso
import com.darkminstrel.pocketdict.models.ParsedTranslation
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow

private const val langFrom = "en"
private const val langTo = "ru"

interface UsecaseTranslate {
    val flowHistory: Flow<History>
    suspend fun getTranslation(query: String): Result<ParsedTranslation>
    suspend fun addToHistory(key: String, value: String)
    suspend fun deleteFromHistory(key: String)
}

class UsecaseTranslateImpl(
    private val apiReverso: ApiReverso,
    private val apiLeo: ApiLeo,
    private val historyRepository: HistoryRepository,
) : UsecaseTranslate {

    override val flowHistory: Flow<History> = historyRepository.flow

    override suspend fun deleteFromHistory(key: String) {
        historyRepository.deleteItem(key)
    }

    override suspend fun getTranslation(query: String): Result<ParsedTranslation> = withContext(Dispatchers.IO) {
        val deferreds = ArrayList<Deferred<Result<ResponseCommon>>>(2)
        deferreds += async(Dispatchers.IO) {
            val requestReverso = RequestReverso(uiLang = "en", direction = "$langFrom-$langTo", source = query)
            runCatching { apiReverso.getTranslation(requestReverso) }
        }
        if (langFrom == "en" && langTo == "ru") {
            deferreds += async(Dispatchers.IO) {
                val requestLeo = RequestLeo(word = query)
                runCatching { apiLeo.getTranslation(requestLeo) }
            }
        }
        deferreds.awaitAll()
            .map { result ->
                result.fold(
                    onSuccess = { it.mapToDomainModel() },
                    onFailure = { Result.failure(it) }
                )
            }.reduce { first, second ->
                val t1 = first.getOrNull()
                val t2 = second.getOrNull()
                when {
                    t1 != null && t2 != null -> Result.success(t1.mergeWith(t2))
                    t1 != null -> first
                    t2 != null -> second
                    else -> first
                }
            }
    }

    override suspend fun addToHistory(key: String, value: String) {
        historyRepository.addItem(key, value)
    }
}

