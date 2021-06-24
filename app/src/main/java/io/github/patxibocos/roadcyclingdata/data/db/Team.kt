package io.github.patxibocos.roadcyclingdata.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "team",
)
data class Team(
    @ColumnInfo(name = "id")
    @PrimaryKey
    val id: String,
)