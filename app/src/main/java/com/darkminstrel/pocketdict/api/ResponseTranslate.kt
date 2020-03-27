package com.darkminstrel.pocketdict.api

data class ResponseTranslateContext (
    val source:String,
    val target:String,
    val isGood:Boolean
)

data class ResponseTranslateTranslation (
    val translation:String,
    val isRude:Boolean,
    val isSlang:Boolean,
    val isFromDict:Boolean,
    val contexts:List<ResponseTranslateContext>?
)

data class ResponseTranslateSource(
    val source:String,
    val directionFrom:String,
    val directionTo:String,
    val translations:List<ResponseTranslateTranslation>?
)

data class ResponseTranslate(
    val error:Boolean,
    val success:Boolean,
    val message:String?,
    val sources:List<ResponseTranslateSource>?
)