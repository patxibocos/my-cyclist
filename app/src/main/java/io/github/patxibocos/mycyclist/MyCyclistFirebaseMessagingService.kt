package io.github.patxibocos.mycyclist

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import io.github.patxibocos.mycyclist.data.DataRepository
import io.github.patxibocos.mycyclist.data.Race
import io.github.patxibocos.mycyclist.data.Stage
import io.github.patxibocos.mycyclist.data.StageType
import io.github.patxibocos.mycyclist.data.isSingleDay
import io.github.patxibocos.mycyclist.ui.home.LeafScreen
import io.github.patxibocos.mycyclist.ui.home.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

const val CHANNEL_ID = "RESULTS_CHANNEL"

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
@AndroidEntryPoint
class MyCyclistFirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var dataRepository: DataRepository

    private val job = SupervisorJob()

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    override fun onMessageReceived(message: RemoteMessage) {
        CoroutineScope(job).launch {
            if (!dataRepository.refresh()) {
                // If data fails to refresh, we skip the rest
                return@launch
            }
            val (race, stage) = getRaceAndStage(message.data) ?: return@launch
            val winner = stage.result.first().participantId
            val winnerName = if (stage.stageType == StageType.TEAM_TIME_TRIAL) {
                requireNotNull(dataRepository.teams.first().find { it.id == winner }).name
            } else {
                requireNotNull(dataRepository.riders.first().find { it.id == winner }).fullName()
            }
            val stageNumber = race.stages.indexOfFirst { it.id == stage.id } + 1
            val destination = LeafScreen.Race.createRoute(Screen.Races, race.id, stage.id)
            val notificationMessage = if (race.isSingleDay()) {
                getString(R.string.notifications_race_results, winnerName)
            } else {
                getString(R.string.notifications_stage_results, winnerName, stageNumber)
            }
            sendNotification(
                uri = "mycyclist://$destination".toUri(),
                title = race.name,
                text = notificationMessage,
            )
        }
        super.onMessageReceived(message)
    }

    private suspend fun getRaceAndStage(messageData: Map<String, String>): Pair<Race, Stage>? {
        val raceId = messageData["race-id"]
        val stageId = messageData["stage-id"]
        val race = dataRepository.races.first().find { it.id == raceId } ?: return null
        val stage = race.stages.find { it.id == stageId } ?: return null
        return race to stage
    }

    private fun sendNotification(uri: Uri, title: String, text: String) {
        val activityActionIntent =
            Intent(
                Intent.ACTION_VIEW,
                uri,
                application,
                MainActivity::class.java,
            )
        val resultsPendingIntent: PendingIntent =
            PendingIntent.getActivity(
                application,
                Random.nextInt(),
                activityActionIntent,
                FLAG_IMMUTABLE,
            )
        val notification =
            NotificationCompat.Builder(this@MyCyclistFirebaseMessagingService, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.btn_star)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(resultsPendingIntent)
                .build()
        with(NotificationManagerCompat.from(this@MyCyclistFirebaseMessagingService)) {
            notify(Random.nextInt(), notification)
        }
    }
}
