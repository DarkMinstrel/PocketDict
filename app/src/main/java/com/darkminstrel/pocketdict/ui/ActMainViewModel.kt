package com.darkminstrel.pocketdict.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.darkminstrel.pocketdict.DBG
import com.darkminstrel.pocketdict.api.ApiResult
import com.darkminstrel.pocketdict.data.ParsedTranslation
import com.darkminstrel.pocketdict.usecases.UsecaseTranslate
import kotlinx.coroutines.*

class ActMainViewModel(private val usecase: UsecaseTranslate) : ViewModel() {

    private val liveDataResult = MutableLiveData<ViewStateTranslate>().apply { value = ViewStateTranslate.Empty }
    fun getLiveDataTranslate() = liveDataResult as LiveData<ViewStateTranslate>

    private var job: Job? = null

    fun clearSearch(){
        job?.cancel()
        job = null
        liveDataResult.value = ViewStateTranslate.Empty
    }

    fun onQuerySubmit(query: String) {
        liveDataResult.value = ViewStateTranslate.Progress
        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            val result = ApiResult.from { usecase.query(query) }
            val viewState = ViewStateTranslate.from(result)
            withContext(Dispatchers.Main) {
                liveDataResult.value = viewState
            }
        }
    }

    override fun onCleared() {
        job?.cancel()
        job = null
        super.onCleared()
    }
}