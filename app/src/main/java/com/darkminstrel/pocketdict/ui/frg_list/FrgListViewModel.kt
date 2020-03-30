package com.darkminstrel.pocketdict.ui.frg_list

import androidx.lifecycle.ViewModel
import com.darkminstrel.pocketdict.usecases.UsecaseTranslate

class FrgListViewModel(private val usecase: UsecaseTranslate) : ViewModel() {

    fun getLiveDataCacheKeys() = usecase.getFavoriteKeys()

    suspend fun getFavorite(key:String) = usecase.getFavorite(key)

}