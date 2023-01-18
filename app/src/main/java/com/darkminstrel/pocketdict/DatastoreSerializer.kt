@file:Suppress("BlockingMethodInNonBlockingContext")

package com.darkminstrel.pocketdict

import androidx.datastore.core.Serializer
import com.squareup.moshi.Moshi
import java.io.InputStream
import java.io.OutputStream

class DatastoreSerializer<T>(
    moshi: Moshi,
    override val defaultValue: T,
    clazz: Class<T>,
) : Serializer<T> {
    private val adapter = moshi.adapter(clazz)

    override suspend fun readFrom(input: InputStream): T {
        val json = input.bufferedReader().use { it.readText() }
        return adapter.fromJson(json) ?: defaultValue
    }

    override suspend fun writeTo(t: T, output: OutputStream) {
        output.bufferedWriter().use {
            it.write(adapter.toJson(t))
        }
    }
}

inline fun <reified T> datastoreSerializer(moshi: Moshi, defaultValue: T): Serializer<T> {
    return DatastoreSerializer(moshi, defaultValue, T::class.java)
}

