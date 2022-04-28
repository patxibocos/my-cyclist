package io.github.patxibocos.mycyclist.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.patxibocos.mycyclist.DefaultDispatcher
import io.github.patxibocos.mycyclist.data.protobuf.FirebaseDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataModule {

    @Singleton
    @Provides
    fun provideDataRepository(@DefaultDispatcher defaultDispatcher: CoroutineDispatcher): DataRepository =
        FirebaseDataRepository(defaultDispatcher)
}
