package com.darkminstrel.pocketdict.utils

import kotlinx.coroutines.delay

class Debouncer(private val debounceTime:Long) {
    private var lastTs = 0L //ms

    suspend fun debounce(){
        val now = System.currentTimeMillis()
        if(now-lastTs<debounceTime) delay(debounceTime-(now-lastTs))
        lastTs = now
    }
}