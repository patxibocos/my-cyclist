package io.github.patxibocos.roadcyclingdata.data.db

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

class Converters {

    private val listOfStringSerializer = ListSerializer(String.serializer())

    @TypeConverter
    fun fromString(value: String): List<String> =
        Json.decodeFromString(listOfStringSerializer, value)

    @TypeConverter
    fun fromList(list: List<String>): String {
        return Json.encodeToString(listOfStringSerializer, list)
    }

}