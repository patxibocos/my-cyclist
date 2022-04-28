package io.github.patxibocos.mycyclist

import android.app.Application
import coil.Coil
import coil.ImageLoader
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltAndroidApp
class MyCyclistApplication : Application() {

    @Inject
    @DefaultDispatcher
    lateinit var defaultDispatcher: CoroutineDispatcher

    override fun onCreate() {
        super.onCreate()
        Coil.setImageLoader {
            ImageLoader.Builder(this)
                .dispatcher(defaultDispatcher)
                .crossfade(true)
                .build()
        }
    }
}
