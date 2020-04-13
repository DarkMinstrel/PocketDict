package com.darkminstrel.pocketdict.api

import com.darkminstrel.pocketdict.data.ParsedTranslation

interface ResponseCommon {
    fun toParsed():ParsedTranslation?
    fun getErrorMessage():String?
}