package com.minizuure.todoplannereducationedition.services.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.util.Log
import androidx.core.app.NotificationCompat
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.first_layer.TaskManagementActivity
import com.minizuure.todoplannereducationedition.first_layer.TaskManagementActivity.Companion.OPEN_DETAIL
import com.minizuure.todoplannereducationedition.first_layer.TaskManagementActivity.Companion.OPEN_DETAIL_GO_TO_PACK
import com.minizuure.todoplannereducationedition.first_layer.TaskManagementActivity.Companion.OPEN_DETAIL_GO_TO_QUIZ
import com.minizuure.todoplannereducationedition.model.ParcelableZoneDateTime
import com.minizuure.todoplannereducationedition.services.database.CATEGORY_QUIZ
import com.minizuure.todoplannereducationedition.services.database.CATEGORY_TO_PACK

const val CHANNEL_ID = "TODO_PLANNER_EDUCATION_EDITION_BY_MINIZUURE_NOTIFICATIONS"
const val KEY_NOTIFICATION_ID = "KEY_NOTIFICATION_ID"
const val KEY_NOTIFICATION_ACTION = "KEY_NOTIFICATION_ACTION"
const val KEY_NOTIFICATION_TASK_ID = "KEY_NOTIFICATION_TASK_ID"
const val KEY_NOTIFICATION_TASKNAME = "KEY_NOTIFICATION_TASKNAME"
const val KEY_NOTIFICATION_MESSAGE = "KEY_NOTIFICATION_MESSAGE"
const val KEY_NOTIFICATION_TASK_DATE_IDENTIFICATION = "KEY_NOTIFICATION_TASK_DATE_IDENTIFICATION"
class AlarmBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            // TODO: [FS/P01] Reschedule all alarms
            println("AlarmBroadcastReceiver: BOOT_COMPLETED")

        } else {
            // TODO: [FS/P01] Show notification

            val notificationId = intent?.getIntExtra(KEY_NOTIFICATION_ID, 0) ?: return
            val action = intent.getStringExtra(KEY_NOTIFICATION_ACTION) ?: return
            val taskID = intent.getLongExtra(KEY_NOTIFICATION_TASK_ID, -1L)
            val taskName = intent.getStringExtra(KEY_NOTIFICATION_TASKNAME) ?: return
            val message = intent.getStringExtra(KEY_NOTIFICATION_MESSAGE) ?: return
            val taskDateIdentification =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(KEY_NOTIFICATION_TASK_DATE_IDENTIFICATION, ParcelableZoneDateTime::class.java)
                } else {
                    intent.getParcelableExtra(KEY_NOTIFICATION_TASK_DATE_IDENTIFICATION)
                } ?: return

            Log.d("AlarmBroadcastReceiver", "onReceive triggered: ${intent.action}" +
                    "\nnotificationId: $notificationId" +
                    "\naction: $action" +
                    "\ntaskName: $taskName"
            )

            val actionToOpen = when (action) {
                CATEGORY_QUIZ -> OPEN_DETAIL_GO_TO_QUIZ
                CATEGORY_TO_PACK -> OPEN_DETAIL_GO_TO_PACK
                else -> OPEN_DETAIL
            }


            val intentToNotes = Intent(
                context,
                TaskManagementActivity::class.java
            ).apply {
                putExtra("action_to_open", actionToOpen)
                putExtra("id", taskID)
                putExtra("title", taskName)
                putExtra("selectedDatetimeISO", taskDateIdentification)
            }


            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_emblem)
                .setLargeIcon(
                    Icon.createWithResource(
                        context,
                        R.mipmap.ic_launcher_foreground
                    )
                )
                .setContentTitle(buildTitle(action, taskName))
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(
                    R.drawable.ic_open_in_new,
                    "Open Task",
                    PendingIntent.getActivity(
                        context,
                        0,
                        intentToNotes,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                )
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