package io.github.patxibocos.mycyclist.data

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
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
    fun provideDataRepository(
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
        firebaseRemoteConfig: FirebaseRemoteConfig,
    ): DataRepository =
        FirebaseDataRepository(defaultDispatcher, firebaseRemoteConfig)

    @Singleton
    @Provides
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig = Firebase.remoteConfig
}
