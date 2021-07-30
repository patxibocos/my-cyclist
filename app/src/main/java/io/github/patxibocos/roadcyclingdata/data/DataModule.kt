package io.github.patxibocos.roadcyclingdata.data

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.patxibocos.roadcyclingdata.data.json.JsonTeamsWithRidersRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataModule {

    @Singleton
    @Provides
    fun provideTeamsWithRidersRepository(
        @ApplicationContext context: Context
    ): TeamsAndRidersRepository {
        return JsonTeamsWithRidersRepository(context)
    }
}
