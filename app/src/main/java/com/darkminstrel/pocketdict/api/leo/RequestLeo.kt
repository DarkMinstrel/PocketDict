package com.darkminstrel.pocketdict.api.leo

data class RequestLeo(
    val word: String,
    val include_media: Int = 0,
    val add_word_forms: Int = 1,
    val port: Int = 1001,
)
