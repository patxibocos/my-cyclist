package io.github.patxibocos.roadcyclingdata.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RiderDao {

    @Query("SELECT * FROM team")
    fun getTeams(): Flow<List<Rider>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(riders: List<Rider>)

}