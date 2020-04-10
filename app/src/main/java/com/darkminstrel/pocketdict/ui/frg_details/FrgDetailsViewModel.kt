package com.darkminstrel.pocketdict.ui.frg_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkminstrel.pocketdict.TextToSpeechManager
import com.darkminstrel.pocketdict.data.ParsedTranslation
import com.darkminstrel.pocketdict.data.ViewStateTranslate
import com.darkminstrel.pocketdict.usecases.UsecaseTranslate
import kotlinx.coroutines.*

class FrgDetailsViewModel(private val usecase: UsecaseTranslate, val ttsManager:TextToSpeechManager) : ViewModel() {

    private val liveDataViewState = MutableLiveData<ViewStateTranslate>()
    fun getLiveDataViewState() = liveDataViewState as LiveData<ViewStateTranslate>

    fun getLiveDataCacheKeys() = usecase.getFavoriteKeys()

    private var job: Job? = null

    fun onQuerySubmit(query: String) {
        liveDataViewState.value = ViewStateTranslate.Progress
        job?.cancel()
        job = viewModelScope.launch {
            val viewState = usecase.getTranslation(query)
            if(isActive) liveDataViewState.postValue(viewState)
        }
    }

    fun onChangeFavoriteStatus(translation: ParsedTranslation, isFavorite:Boolean){
        viewModelScope.launch {
            usecase.setFavorite(translation, isFavorite)
        }
    }

    override fun onCleared() {
        job?.cancel()
        job = null
        super.onCleared()
    }

}