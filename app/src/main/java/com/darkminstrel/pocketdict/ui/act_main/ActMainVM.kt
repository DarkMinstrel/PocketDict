package com.darkminstrel.pocketdict.ui.act_main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkminstrel.pocketdict.TextToSpeechManager
import com.darkminstrel.pocketdict.data.ParsedTranslation
import com.darkminstrel.pocketdict.data.ViewStateTranslate
import com.darkminstrel.pocketdict.usecases.UsecaseTranslate
import kotlinx.coroutines.*

class ActMainVM(private val usecase: UsecaseTranslate, val ttsManager: TextToSpeechManager) : ViewModel() {

    val liveDataFavoriteKeys = usecase.liveDataFavoriteKeys
    suspend fun getFavorite(key:String) = usecase.getFavorite(key)

    private val ldViewState = MutableLiveData<ViewStateTranslate?>().apply {
        value = null
    }
    val liveDataViewState = ldViewState as LiveData<ViewStateTranslate?>

    private var job: Job? = null

    fun tryReset():Boolean{
        job?.cancel()
        job = null
        if(ldViewState.value!=null) {
            ldViewState.value = null
            return true
        }else{
            return false
        }
    }

    fun onQueryChanged(queryTrimmed: String) {
        job?.cancel()
        job = null

        val shouldShowSuggestion = liveDataFavoriteKeys.value?.any { it!=queryTrimmed && it.contains(queryTrimmed) } ?: false
        if(shouldShowSuggestion){
            ldViewState.value = null
        }else{
            job = load(queryTrimmed)
        }
    }

    fun onQuerySubmit(queryTrimmed: String){
        job?.cancel()
        job = load(queryTrimmed)
    }

    private fun load(queryTrimmed: String):Job = viewModelScope.launch {
        ldViewState.value = ViewStateTranslate.Progress
        val translation = usecase.getTranslation(queryTrimmed)
        withContext(Dispatchers.Main){
            ensureActive()
            ldViewState.value = translation
        }
    }

    fun onOpenDetails(translation: ParsedTranslation){
        ldViewState.value = ViewStateTranslate.Data(translation)
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