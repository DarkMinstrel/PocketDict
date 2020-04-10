package com.darkminstrel.pocketdict.database.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface RoomDao {
    @Query("SELECT `key` FROM RoomEntity ORDER BY `rowid` DESC")
    fun getAllKeys(): LiveData<List<String>>

    @Query("INSERT INTO RoomEntity (`key`,`value`) VALUES (:key, :value)")
    fun put(key:String, value:String)

    @Query("SELECT value FROM RoomEntity WHERE `key` = :key")
    fun get(key: String): String?

    @Query("DELETE FROM RoomEntity WHERE `key` = :key")
    fun deleteKey(key:String)
}