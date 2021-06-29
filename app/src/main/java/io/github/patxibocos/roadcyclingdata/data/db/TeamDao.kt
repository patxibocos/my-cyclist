package io.github.patxibocos.roadcyclingdata.data.db

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDao {

    @Query("SELECT * FROM team")
    fun getTeams(): Flow<List<Team>>

}