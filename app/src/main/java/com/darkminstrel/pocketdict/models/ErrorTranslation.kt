package com.darkminstrel.pocketdict.models

import java.io.IOException

abstract class ErrorTranslation: IOException()
class ErrorTranslationHttp(val httpCode: Int) : ErrorTranslation()
class ErrorTranslationServer(val serverMessage: String) : ErrorTranslation()
object ErrorTranslationEmpty : ErrorTranslation()

