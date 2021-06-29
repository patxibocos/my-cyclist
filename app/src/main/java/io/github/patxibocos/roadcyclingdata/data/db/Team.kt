package io.github.patxibocos.roadcyclingdata.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "team"
)
data class Team(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "status")
    val status: String,
    @ColumnInfo(name = "abbreviation")
    val abbreviation: String,
    @ColumnInfo(name = "country")
    val country: String,
    @ColumnInfo(name = "bike")
    val bike: String,
    @ColumnInfo(name = "jersey")
    val jersey: String,
    @ColumnInfo(name = "website")
    val website: String?,
    @ColumnInfo(name = "year")
    val year: Int,
    @ColumnInfo(name = "riders")
    val riders: List<String>,
)