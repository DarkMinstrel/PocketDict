package com.darkminstrel.pocketdict.database

import android.content.Context
import com.darkminstrel.pocketdict.data.ParsedTranslation
import com.snappydb.DBFactory
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseSnappy(context: Context):Database {
    private val moshiAdapter = Moshi.Builder().build().adapter(ParsedTranslation::class.java)
    private val snappyDB = DBFactory.open(context, "translations")

    override suspend fun hasKey(key: String): Boolean {
        return snappyDB.exists(key)
    }

    override suspend fun getAllKeys(): List<String> {
        val list = ArrayList<String>()
        val iterator = snappyDB.allKeysReverseIterator()
        while(iterator.hasNext()) list += iterator.next(1000000)
        return list
    }

    override suspend fun get(key: String): ParsedTranslation? {
        return try{
            withContext(Dispatchers.IO) {
                moshiAdapter.fromJson(snappyDB.get(key))
            }
        }catch (e:Exception){
            null
        }
    }

    override suspend fun put(key: String, value: ParsedTranslation) {
        val json = moshiAdapter.toJson(value)
        snappyDB.put(key, json)
    }

    override suspend fun delete(key: String) {
        snappyDB.del(key)
    }
}