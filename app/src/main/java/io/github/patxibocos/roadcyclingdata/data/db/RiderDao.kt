package io.github.patxibocos.roadcyclingdata.data.db

import androidx.room.Dao
import androidx.room.Query

@Dao
interface RiderDao {

    @Query("SELECT * FROM rider")
    suspend fun getRiders(): List<Rider>

}