package io.github.patxibocos.mycyclist

import android.app.Application
import coil.Coil
import coil.ImageLoader
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers

@HiltAndroidApp
class MyCyclistApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Coil.setImageLoader {
            ImageLoader.Builder(this)
                .dispatcher(Dispatchers.Default)
                .crossfade(true)
                .build()
        }
    }
}
