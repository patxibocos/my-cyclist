package io.github.patxibocos.roadcyclingdata.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "rider"
)
data class Rider(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "first_name")
    val firstName: String,
    @ColumnInfo(name = "last_name")
    val lastName: String,
    @ColumnInfo(name = "country")
    val country: String,
    @ColumnInfo(name = "website")
    val website: String?,
    @ColumnInfo(name = "birth_date")
    val birthDate: LocalDate,
    @ColumnInfo(name = "birth_place")
    val birthPlace: String?,
    @ColumnInfo(name = "weight")
    val weight: Int?,
    @ColumnInfo(name = "height")
    val height: Int?,
    @ColumnInfo(name = "photo")
    val photo: String,
)