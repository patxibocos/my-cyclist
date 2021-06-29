package io.github.patxibocos.roadcyclingdata.data.db

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Converters {

    private val listOfStringSerializer = ListSerializer(String.serializer())

    @TypeConverter
    fun stringToList(value: String): List<String> =
        Json.decodeFromString(listOfStringSerializer, value)

    @TypeConverter
    fun listToString(list: List<String>): String =
        Json.encodeToString(listOfStringSerializer, list)

    @TypeConverter
    fun stringToLocalDate(value: String): LocalDate =
        LocalDate.parse(value)

    @TypeConverter
    fun localDateToString(localDate: LocalDate): String =
        localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)

}