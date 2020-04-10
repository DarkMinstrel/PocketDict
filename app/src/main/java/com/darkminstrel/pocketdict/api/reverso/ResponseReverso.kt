package com.darkminstrel.pocketdict.api.reverso

data class ResponseReversoContext (
    val source:String,
    val target:String,
    val isGood:Boolean
)

data class ResponseReversoTranslation (
    val translation:String,
    val isRude:Boolean,
    val isSlang:Boolean,
    val isFromDict:Boolean,
    val contexts:List<ResponseReversoContext>?
)

data class ResponseReversoSource(
    val source:String,
    val directionFrom:String,
    val directionTo:String,
    val translations:List<ResponseReversoTranslation>?
)

data class ResponseReverso(
    val error:Boolean,
    val success:Boolean,
    val message:String?,
    val sources:List<ResponseReversoSource>?
)