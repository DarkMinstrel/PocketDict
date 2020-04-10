package com.darkminstrel.pocketdict.api.reverso

data class RequestReverso(
    val deviceId: String = "",
    val origin: String = "",
    val uiLang: String,
    val accessToken: String = "",
    val direction: String,
    val source: String,
    val pageUrl: String = "",
    val pageTitle: String = "",
    val reversoPage: String? = null,
    val appId: String = "" //"51f29f9f-0f7f-cc01-c3ae-62fa14f606ad"
)