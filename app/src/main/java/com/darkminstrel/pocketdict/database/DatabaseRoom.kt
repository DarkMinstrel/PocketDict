package com.darkminstrel.pocketdict.database

import android.content.Context
import androidx.room.Room
import com.darkminstrel.pocketdict.data.ParsedTranslation
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseRoom(context: Context):Databaseable {
    private val moshiAdapter = Moshi.Builder().build().adapter(ParsedTranslation::class.java)
    private val db = Room.databaseBuilder(context, RoomDB::class.java, "translations.db").build()

    override suspend fun getAllKeys(): List<String> {
        return db.getDao().getAllKeys()
    }

    override suspend fun get(key: String): ParsedTranslation? {
        val json: String = db.getDao().get(key) ?: return null
        return try{
            withContext(Dispatchers.IO) {
                moshiAdapter.fromJson(json)
            }
        }catch (e:Exception){ null }
    }

    override suspend fun put(key: String, value: ParsedTranslation) {
        val json = moshiAdapter.toJson(value)
        db.getDao().put(key, json)
    }

    override suspend fun delete(key: String) {
        db.getDao().deleteKey(key)
    }
}