package io.github.patxibocos.roadcyclingdata.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RiderDao {

    @Query("SELECT * FROM rider WHERE first_name LIKE '%' || :query || '%' COLLATE NOCASE OR last_name LIKE '%' || :query || '%' COLLATE NOCASE ORDER BY id ASC LIMIT :limit OFFSET :offset")
    suspend fun findRiders(query: String, limit: Int, offset: Int): List<Rider>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(riders: List<Rider>)

    @Query("SELECT 1 WHERE 1 = 1")
    fun test(): Int

}