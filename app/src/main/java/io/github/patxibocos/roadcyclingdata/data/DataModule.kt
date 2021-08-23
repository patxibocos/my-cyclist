package io.github.patxibocos.roadcyclingdata.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.patxibocos.roadcyclingdata.data.json.JsonDataRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataModule {

    @Singleton
    @Provides
    fun provideDataRepository(): DataRepository {
        return JsonDataRepository()
    }
}
