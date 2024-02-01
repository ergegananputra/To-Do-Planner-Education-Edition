package com.minizuure.todoplannereducationedition.services.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Parcel
import android.util.Log
import com.minizuure.todoplannereducationedition.ToDoPlannerApplication
import com.minizuure.todoplannereducationedition.model.ParcelableZoneDateTime
import com.minizuure.todoplannereducationedition.services.database.CATEGORY_QUIZ
import com.minizuure.todoplannereducationedition.services.database.CATEGORY_TO_PACK
import com.minizuure.todoplannereducationedition.services.database.queue.NotificationQueueTable
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AndroidAlarmManager(
    private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(itemAlarmQueue: ItemAlarmQueue) {
        Log.d("AndroidAlarmManager", "schedule triggered: ${itemAlarmQueue.taskName}")

        val readableDatetime = DatetimeAppManager(itemAlarmQueue.time).toReadable()
        val preMessage = when (itemAlarmQueue.action) {
            CATEGORY_QUIZ -> {
                "Heads up! In just up to 2 days, you've got a quiz in ${itemAlarmQueue.taskName} on $readableDatetime!\n\n" +
                        itemAlarmQueue.message
            }
            CATEGORY_TO_PACK -> {
                "Don't forget to prepare your pack in ${itemAlarmQueue.taskName} on $readableDatetime!\n\n" +
                        itemAlarmQueue.message
            }
            else -> return
        }

        val message = when (itemAlarmQueue.action) {
            CATEGORY_QUIZ -> {
                "Today, you've got a quiz in ${itemAlarmQueue.taskName}!\n\n" +
                        itemAlarmQueue.message
            }
            CATEGORY_TO_PACK -> {
                "Today, you've got to pack in ${itemAlarmQueue.taskName}!\n\n" +
                        itemAlarmQueue.message
            }
            else -> return
        }

        val parcelableZoneDateTime = ParcelableZoneDateTime(itemAlarmQueue.taskDateIdentification)
        val parcel = Parcel.obtain()
        parcelableZoneDateTime.writeToParcel(parcel, 0)

        val intentPrep = Intent(context, AlarmBroadcastReceiver::class.java).apply {
            putExtra(KEY_NOTIFICATION_ID, ItemAlarmQueue(itemAlarmQueue.id).convertIdToDaysBeforeID())
            putExtra(KEY_NOTIFICATION_ACTION, itemAlarmQueue.action)
            putExtra(KEY_NOTIFICATION_TASK_ID, itemAlarmQueue.taskId)
            putExtra(KEY_NOTIFICATION_TASKNAME, itemAlarmQueue.taskName)
            putExtra(KEY_NOTIFICATION_MESSAGE, preMessage)
            putExtra(KEY_NOTIFICATION_TASK_DATE_IDENTIFICATION, parcelableZoneDateTime)
            action = "com.minizuure.todoplannereducationedition.services.notification.ALARM"
        }

        val intent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
            putExtra(KEY_NOTIFICATION_ID, itemAlarmQueue.id)
            putExtra(KEY_NOTIFICATION_ACTION, itemAlarmQueue.action)
            putExtra(KEY_NOTIFICATION_TASK_ID, itemAlarmQueue.taskId)
            putExtra(KEY_NOTIFICATION_TASKNAME, itemAlarmQueue.taskName)
            putExtra(KEY_NOTIFICATION_MESSAGE, message)
            putExtra(KEY_NOTIFICATION_TASK_DATE_IDENTIFICATION, parcelableZoneDateTime)
            action = "com.minizuure.todoplannereducationedition.services.notification.ALARM"
        }


        val dDay = DatetimeAppManager(itemAlarmQueue.time).selectedDetailDatetimeISO

        // Alarm Notification 2 days before
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            dDay.minusDays(2).toEpochSecond() * 1000,
            PendingIntent.getBroadcast(
                context,
                ItemAlarmQueue(itemAlarmQueue.id).convertIdToDaysBeforeID(),
                intentPrep,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        )

        // Alarm Notification D-Day
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            dDay.toEpochSecond() * 1000,
            PendingIntent.getBroadcast(
                context,
                itemAlarmQueue.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )


        // Save to database
        val app = context.applicationContext as ToDoPlannerApplication
        val notificationQueue = app.appDb.notificationQueueTableDao()

        CoroutineScope(Dispatchers.IO).launch {
            notificationQueue.insert(NotificationQueueTable.convertItemToTable(itemAlarmQueue))

            val itemAlarmQueuePending = itemAlarmQueue.copy(
                id = ItemAlarmQueue(itemAlarmQueue.id).convertIdToDaysBeforeID(),
                message = preMessage
            )

            notificationQueue.insert(NotificationQueueTable.convertItemToTable(itemAlarmQueuePending))
        }
    }

    override fun cancel(itemAlarmQueue: ItemAlarmQueue) {

        val intent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
            putExtra(KEY_NOTIFICATION_ID, itemAlarmQueue.id)
            putExtra(KEY_NOTIFICATION_ACTION, itemAlarmQueue.action)
            putExtra(KEY_NOTIFICATION_TASKNAME, itemAlarmQueue.taskName)
            action = "com.minizuure.todoplannereducationedition.services.notification.ALARM"
        }

        val intentPrep = Intent(context, AlarmBroadcastReceiver::class.java).apply {
            putExtra(KEY_NOTIFICATION_ID, ItemAlarmQueue(itemAlarmQueue.id).convertIdToDaysBeforeID())
            putExtra(KEY_NOTIFICATION_ACTION, itemAlarmQueue.action)
            putExtra(KEY_NOTIFICATION_TASKNAME, itemAlarmQueue.taskName)
            action = "com.minizuure.todoplannereducationedition.services.notification.ALARM"
        }

        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                itemAlarmQueue.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )

        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                ItemAlarmQueue(itemAlarmQueue.id).convertIdToDaysBeforeID(),
                intentPrep,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )

    }


}