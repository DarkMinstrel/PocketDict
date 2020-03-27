package com.darkminstrel.pocketdict.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.darkminstrel.pocketdict.api.ApiResult
import com.darkminstrel.pocketdict.data.ParsedTranslation
import com.darkminstrel.pocketdict.usecases.UsecaseTranslate
import kotlinx.coroutines.*

class ActMainViewModel(private val usecase: UsecaseTranslate) : ViewModel() {
    private val db = usecase.db

    private val liveDataResult = MutableLiveData<ViewStateTranslate>().apply { value = ViewStateTranslate.Empty }
    fun getLiveDataTranslate() = liveDataResult as LiveData<ViewStateTranslate>

    private val liveDataCacheKeys = MutableLiveData<List<String>>()
    fun getLiveDataCacheKeys() = liveDataCacheKeys as LiveData<List<String>>

    init {
        CoroutineScope(Dispatchers.IO).launch {
            liveDataCacheKeys.postValue(db.getAllKeys())
        }
    }

    private var job: Job? = null

    fun clearSearch(){
        job?.cancel()
        job = null
        if(liveDataResult.value!=ViewStateTranslate.Empty) liveDataResult.value = ViewStateTranslate.Empty
    }

    fun onQuerySubmit(query: String) {
        liveDataResult.value = ViewStateTranslate.Progress
        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            val cache = db.get(query)
            if(cache!=null){
                if(isActive) liveDataResult.postValue(ViewStateTranslate.Data(cache))
            }else{
                val apiResult = ApiResult.from { usecase.query(query) }
                val viewState = ViewStateTranslate.from(apiResult)
                if(isActive) liveDataResult.postValue(viewState)
            }
        }
    }

    fun onChangeFavoriteStatus(translation: ParsedTranslation, isFavorite:Boolean){
        CoroutineScope(Dispatchers.IO).launch {
            if(isFavorite) db.put(translation.source, translation)
            else db.delete(translation.source)
            if(isActive) liveDataCacheKeys.postValue(db.getAllKeys())
        }
    }

    suspend fun getCachedTranslation(key:String) = db.get(key)

    override fun onCleared() {
        job?.cancel()
        job = null
        super.onCleared()
    }
}