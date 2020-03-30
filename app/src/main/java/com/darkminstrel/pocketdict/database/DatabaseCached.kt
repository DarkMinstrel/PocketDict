package com.darkminstrel.pocketdict.database

import android.util.LruCache
import com.darkminstrel.pocketdict.data.ParsedTranslation
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class DatabaseCached(private val db:Databaseable):Databaseable {

    private val lruCache = LruCache<String, ParsedTranslation>(30)
    private val lock = ReentrantReadWriteLock()

    override suspend fun getAllKeys(): List<String> {
        return lock.read { db.getAllKeys() }
    }

    override suspend fun put(key: String, value: ParsedTranslation) {
        lock.write {
            db.put(key, value)
            lruCache.put(key, value)
        }
    }

    override suspend fun delete(key: String){
        lock.write {
            db.delete(key)
            lruCache.remove(key)
        }
    }

    override suspend fun get(key: String): ParsedTranslation? {
        return lock.read { lruCache.get(key) ?: db.get(key)?.also{ lruCache.put(key, it) } }
    }

}