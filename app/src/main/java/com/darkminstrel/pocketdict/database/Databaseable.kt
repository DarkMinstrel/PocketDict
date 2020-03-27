package com.darkminstrel.pocketdict.database

import com.darkminstrel.pocketdict.data.ParsedTranslation

interface Databaseable {
    suspend fun getAllKeys():List<String>
    suspend fun get(key:String): ParsedTranslation?
    suspend fun put(key:String, value:ParsedTranslation)
    suspend fun delete(key:String)
}