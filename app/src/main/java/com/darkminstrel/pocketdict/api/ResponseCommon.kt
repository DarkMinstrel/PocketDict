package com.darkminstrel.pocketdict.api

import com.darkminstrel.pocketdict.models.ParsedTranslation

interface ResponseCommon {
    fun mapToDomainModel(): Result<ParsedTranslation>
}