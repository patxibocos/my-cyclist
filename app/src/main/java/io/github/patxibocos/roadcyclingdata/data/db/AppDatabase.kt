package io.github.patxibocos.roadcyclingdata.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Team::class, Rider::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun teamsDao(): TeamDao
    abstract fun ridersDao(): RiderDao

}