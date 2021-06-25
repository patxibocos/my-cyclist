package io.github.patxibocos.roadcyclingdata.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RiderDao {

    @Query("SELECT * FROM rider")
    fun getRiders(): Flow<List<Rider>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(riders: List<Rider>)

    @Query("SELECT 1 WHERE 1 = 1")
    fun test(): Int

}