package com.darkminstrel.pocketdict.database.room

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.darkminstrel.pocketdict.data.ParsedTranslation
import com.darkminstrel.pocketdict.database.Databaseable
import com.squareup.moshi.Moshi

class DatabaseRoom(context: Context, moshi: Moshi): Databaseable {
    companion object {
        fun build(context: Context, moshi: Moshi): Databaseable = DatabaseRoom(context, moshi)
    }

    private val moshiAdapter = moshi.adapter(ParsedTranslation::class.java)
    private val db = Room.databaseBuilder(context, RoomDB::class.java, "translations.db").build()

    override fun getAllKeys(): LiveData<List<String>> = db.getDao().getAllKeys()

    override suspend fun get(key: String): ParsedTranslation? {
        val json:String = db.getDao().get(key) ?: return null
        return try{
            @Suppress("BlockingMethodInNonBlockingContext")
            moshiAdapter.fromJson(json)
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