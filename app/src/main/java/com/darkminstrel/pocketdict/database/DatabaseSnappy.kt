package com.darkminstrel.pocketdict.database

import android.content.Context
import com.darkminstrel.pocketdict.data.ParsedTranslation
import com.snappydb.DBFactory

class DatabaseSnappy(context: Context):Database {
    private val snappyDB = DBFactory.open(context, "translations")

    override suspend fun hasKey(key: String): Boolean {
        return snappyDB.exists(key)
    }

    override suspend fun get(key: String): ParsedTranslation? {
        return try{
            snappyDB.getObject(key, ParsedTranslation::class.java)
        }catch (e:Exception){
            null
        }
    }

    override suspend fun put(key: String, value: ParsedTranslation) {
        snappyDB.put(key, value)
    }
}