package com.minizuure.todoplannereducationedition.services.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.services.database.CATEGORY_QUIZ
import com.minizuure.todoplannereducationedition.services.database.CATEGORY_TO_PACK

const val CHANNEL_ID = "TODO_PLANNER_EDUCATION_EDITION_BY_MINIZUURE_NOTIFICATIONS"
const val KEY_NOTIFICATION_ID = "KEY_NOTIFICATION_ID"
const val KEY_NOTIFICATION_ACTION = "KEY_NOTIFICATION_ACTION"
const val KEY_NOTIFICATION_TASKNAME = "KEY_NOTIFICATION_TASKNAME"
const val KEY_NOTIFICATION_MESSAGE = "KEY_NOTIFICATION_MESSAGE"
class AlarmBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            // TODO: [FS/P01] Reschedule all alarms
            println("AlarmBroadcastReceiver: BOOT_COMPLETED")

        } else {
            // TODO: [FS/P01] Show notification

            val notificationId = intent?.getIntExtra(KEY_NOTIFICATION_ID, 0) ?: return
            val action = intent.getStringExtra(KEY_NOTIFICATION_ACTION) ?: return
            val taskName = intent.getStringExtra(KEY_NOTIFICATION_TASKNAME) ?: return
            val message = intent.getStringExtra(KEY_NOTIFICATION_MESSAGE) ?: return

            Log.d("AlarmBroadcastReceiver", "onReceive triggered: ${intent.action}" +
                    "\nnotificationId: $notificationId" +
                    "\naction: $action" +
                    "\ntaskName: $taskName"
            )


            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_to_do_planner_education_edition)
                .setContentTitle(buildTitle(action, taskName))
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

            notificationManager.notify(notificationId, notification)
        }
    }

    private fun buildTitle(action: String, taskName: String) : String {
        return when (action) {
            CATEGORY_QUIZ -> "\uD83D\uDD14 Quiz Alert - $taskName \uD83D\uDCDA"
            CATEGORY_TO_PACK -> "\uD83D\uDD14 To-Pack Alert - $taskName \uD83D\uDECD\uFE0F"
            else -> "Alert! - $taskName"
        }
    }
}