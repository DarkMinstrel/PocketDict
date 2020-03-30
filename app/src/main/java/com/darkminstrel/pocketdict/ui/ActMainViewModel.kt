package com.darkminstrel.pocketdict.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.darkminstrel.pocketdict.data.ParsedTranslation
import com.darkminstrel.pocketdict.usecases.UsecaseTranslate
import kotlinx.coroutines.*

class ActMainViewModel(private val usecase: UsecaseTranslate) : ViewModel() {

    private val liveDataViewState = MutableLiveData<ViewStateTranslate>().apply { value = ViewStateTranslate.Empty }
    fun getLiveDataViewState() = liveDataViewState as LiveData<ViewStateTranslate>

    private val liveDataCacheKeys = MutableLiveData<List<String>>()
    fun getLiveDataCacheKeys() = liveDataCacheKeys as LiveData<List<String>>

    init {
        CoroutineScope(Dispatchers.IO).launch {
            liveDataCacheKeys.postValue(usecase.getFavoriteKeys())
        }
    }

    private var job: Job? = null

    fun clearSearch(){
        job?.cancel()
        job = null
        if(liveDataViewState.value!=ViewStateTranslate.Empty) liveDataViewState.value = ViewStateTranslate.Empty
    }

    fun onQuerySubmit(query: String) {
        liveDataViewState.value = ViewStateTranslate.Progress
        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            val viewState = usecase.getTranslation(query)
            if(isActive) liveDataViewState.postValue(viewState)
        }
    }

    fun onChangeFavoriteStatus(translation: ParsedTranslation, isFavorite:Boolean){
        CoroutineScope(Dispatchers.IO).launch {
            usecase.setFavorite(translation, isFavorite)
            if(isActive) liveDataCacheKeys.postValue(usecase.getFavoriteKeys())
        }
    }

    suspend fun getFavorite(key:String) = usecase.getFavorite(key)

    override fun onCleared() {
        job?.cancel()
        job = null
        super.onCleared()
    }
}