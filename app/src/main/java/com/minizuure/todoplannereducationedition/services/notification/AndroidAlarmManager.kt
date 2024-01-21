package com.minizuure.todoplannereducationedition.services.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.minizuure.todoplannereducationedition.services.database.CATEGORY_QUIZ
import com.minizuure.todoplannereducationedition.services.database.CATEGORY_TO_PACK
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager

class AndroidAlarmManager(
    private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(itemAlarmQueue: ItemAlarmQueue) {
        val greetingMessage = if (itemAlarmQueue.action == CATEGORY_QUIZ)
            "Quiz Alert!"
        else if (itemAlarmQueue.action == CATEGORY_TO_PACK)
            "To-Pack Alert!"
        else
            return

        val readableDatetime = DatetimeAppManager(itemAlarmQueue.time).toReadable()
        val preMessage = when (itemAlarmQueue.action) {
            CATEGORY_QUIZ -> {
                greetingMessage + "\n\n" +
                        "Heads up! In just up to 2 days, you've got a quiz in ${itemAlarmQueue.taskName} on $readableDatetime!\n\n" +
                        itemAlarmQueue.message
            }
            CATEGORY_TO_PACK -> {
                greetingMessage + "\n\n" +
                        "Don't forget to prepare your pack in ${itemAlarmQueue.taskName} on $readableDatetime!\n\n" +
                        itemAlarmQueue.message
            }
            else -> return
        }

        val message = when (itemAlarmQueue.action) {
            CATEGORY_QUIZ -> {
                greetingMessage + "\n\n" +
                        "Today, you've got a quiz in ${itemAlarmQueue.taskName}!\n\n" +
                        itemAlarmQueue.message
            }
            CATEGORY_TO_PACK -> {
                greetingMessage + "\n\n" +
                        "Today, you've got to pack in ${itemAlarmQueue.taskName}!\n\n" +
                        itemAlarmQueue.message
            }
            else -> return
        }


        val intentPrep = Intent(context, AlarmBroadcastReceiver::class.java).apply {
            putExtra(KEY_NOTIFICATION_ID, itemAlarmQueue.id)
            putExtra(KEY_NOTIFICATION_ACTION, itemAlarmQueue.action)
            putExtra(KEY_NOTIFICATION_TASKNAME, itemAlarmQueue.taskName)
            putExtra(KEY_NOTIFICATION_MESSAGE, preMessage)
            action = "com.minizuure.todoplannereducationedition.services.notification.ALARM"
        }

        val intent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
            putExtra(KEY_NOTIFICATION_ID, itemAlarmQueue.id + 1000)
            putExtra(KEY_NOTIFICATION_ACTION, itemAlarmQueue.action)
            putExtra(KEY_NOTIFICATION_TASKNAME, itemAlarmQueue.taskName)
            putExtra(KEY_NOTIFICATION_MESSAGE, message)
            action = "com.minizuure.todoplannereducationedition.services.notification.ALARM"
        }


        val dDay = DatetimeAppManager(itemAlarmQueue.time).selectedDetailDatetimeISO

        // Alarm Notification 2 days before
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            dDay.minusDays(2).toEpochSecond() * 1000,
            PendingIntent.getBroadcast(
                context,
                itemAlarmQueue.id + 1000,
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
    }

    override fun cancel(itemAlarmQueue: ItemAlarmQueue) {

        val intent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
            putExtra(KEY_NOTIFICATION_ID, itemAlarmQueue.id)
            putExtra(KEY_NOTIFICATION_ACTION, itemAlarmQueue.action)
            putExtra(KEY_NOTIFICATION_TASKNAME, itemAlarmQueue.taskName)
            action = "com.minizuure.todoplannereducationedition.services.notification.ALARM"
        }

        val intentPrep = Intent(context, AlarmBroadcastReceiver::class.java).apply {
            putExtra(KEY_NOTIFICATION_ID, itemAlarmQueue.id + 1000)
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
                itemAlarmQueue.id + 1000,
                intentPrep,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )

    }


}