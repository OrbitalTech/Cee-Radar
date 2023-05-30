package com.orbital.cee.core

import androidx.datastore.core.Serializer
import com.orbital.cee.model.AlarmLessReports
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
object AppSettingsSerializer : Serializer<AlarmLessReports> {
    override val defaultValue: AlarmLessReports
        get() = AlarmLessReports()
    override suspend fun readFrom(input: InputStream): AlarmLessReports {
        return try {
            Json.decodeFromString(
                deserializer = AlarmLessReports.serializer(),
                string = input.readBytes().decodeToString()
            )
        }catch (e : SerializationException){
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: AlarmLessReports, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = AlarmLessReports.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}