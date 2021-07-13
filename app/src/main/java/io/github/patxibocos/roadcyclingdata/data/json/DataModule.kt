package io.github.patxibocos.roadcyclingdata.data.json

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataModule {

    @Singleton
    @Provides
    fun provideTeamsAndRiders(
        @ApplicationContext context: Context
    ): TeamsAndRiders {
        val jsonContent = context.assets.open("2021.json").bufferedReader().use { it.readText() }
        return Json.decodeFromString(jsonContent)
    }

    @Singleton
    @Provides
    fun provideDataProvider(
        @ApplicationContext context: Context
    ): DataProvider {
        return DataProvider(context)
    }

}