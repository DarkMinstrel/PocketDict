package com.darkminstrel.pocketdict.ui.frg_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.darkminstrel.pocketdict.TextToSpeechManager
import com.darkminstrel.pocketdict.data.ParsedTranslation
import com.darkminstrel.pocketdict.usecases.UsecaseTranslate
import com.darkminstrel.pocketdict.data.ViewStateTranslate
import kotlinx.coroutines.*

class FrgDetailsViewModel(private val usecase: UsecaseTranslate, val ttsManager:TextToSpeechManager) : ViewModel() {

    private val liveDataViewState = MutableLiveData<ViewStateTranslate>()
    fun getLiveDataViewState() = liveDataViewState as LiveData<ViewStateTranslate>

    private var job: Job? = null

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
        }
    }

    fun getLiveDataCacheKeys() = usecase.getFavoriteKeys()

    override fun onCleared() {
        job?.cancel()
        job = null
        super.onCleared()
    }

}