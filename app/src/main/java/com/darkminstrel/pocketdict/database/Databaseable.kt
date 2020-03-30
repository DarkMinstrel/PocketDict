package com.darkminstrel.pocketdict.database

import androidx.lifecycle.LiveData
import com.darkminstrel.pocketdict.data.ParsedTranslation

interface Databaseable {
    fun getAllKeys(): LiveData<List<String>>
    suspend fun get(key:String): ParsedTranslation?
    suspend fun put(key:String, value:ParsedTranslation)
    suspend fun delete(key:String)
}