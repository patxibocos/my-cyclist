package io.github.patxibocos.mycyclist

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import coil.Coil
import coil.ImageLoader
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
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
        setupCoil()
        createNotificationChannel()
        subscribeToTopic()
    }

    private fun setupCoil() {
        Coil.setImageLoader {
            ImageLoader.Builder(this)
                .dispatcher(defaultDispatcher)
                .crossfade(true)
                .build()
        }
    }

    private fun subscribeToTopic() {
        Firebase.messaging.subscribeToTopic("stage-results")
    }

    private fun createNotificationChannel() {
        // If the Android Version is greater than Oreo,
        // then create the NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "notifications-channel"

            val channel = NotificationChannel(
                CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications"
            }

            // Register the channel
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
