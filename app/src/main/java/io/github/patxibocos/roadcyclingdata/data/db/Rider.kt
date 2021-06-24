package io.github.patxibocos.roadcyclingdata.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "rider"
)
data class Rider(
    @ColumnInfo(name = "id")
    @PrimaryKey
    val id: String,
)