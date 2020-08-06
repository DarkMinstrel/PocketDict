package com.darkminstrel.pocketdict.ui.act_main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkminstrel.pocketdict.TextToSpeechManager
import com.darkminstrel.pocketdict.data.ParsedTranslation
import com.darkminstrel.pocketdict.data.ViewStateTranslate
import com.darkminstrel.pocketdict.usecases.UsecaseTranslate
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ActMainVM(private val usecase: UsecaseTranslate, val ttsManager: TextToSpeechManager) : ViewModel() {

    val liveDataFavoriteKeys = usecase.liveDataFavoriteKeys
    suspend fun getFavorite(key:String) = usecase.getFavorite(key)

    private val _liveDataViewState = MutableLiveData<ViewStateTranslate?>().apply {
        value = null
    }
    val liveDataViewState = _liveDataViewState as LiveData<ViewStateTranslate?>

    private var job: Job? = null

    fun tryReset():Boolean{
        job?.cancel()
        job = null
        if(_liveDataViewState.value!=null) {
            _liveDataViewState.value = null
            return true
        }else{
            return false
        }
    }

    fun onQuerySubmit(queryTrimmed: String) {
        _liveDataViewState.value = ViewStateTranslate.Progress
        job?.cancel()
        job = viewModelScope.launch {
            val viewState = usecase.getTranslation(queryTrimmed)
            if(isActive) _liveDataViewState.postValue(viewState)
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