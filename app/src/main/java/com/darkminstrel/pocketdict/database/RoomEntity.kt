package com.darkminstrel.pocketdict.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RoomEntity (
    @PrimaryKey
    @ColumnInfo(name = "key")
    var title: String,
    @ColumnInfo(name = "value")
    var content: String
)