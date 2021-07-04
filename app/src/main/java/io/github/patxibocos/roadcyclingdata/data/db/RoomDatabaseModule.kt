package io.github.patxibocos.roadcyclingdata.data.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RoomDatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "road-cycling-data-db")
            .createFromAsset("2021.db")
            .build()
    }

    @Singleton
    @Provides
    fun provideRiderDao(db: AppDatabase) = db.ridersDao()

    @Singleton
    @Provides
    fun provideTeamDao(db: AppDatabase) = db.teamsDao()

}