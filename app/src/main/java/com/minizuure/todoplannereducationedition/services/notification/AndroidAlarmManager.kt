package com.minizuure.todoplannereducationedition.services.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager

class AndroidAlarmManager(
    private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(itemAlarmQueue: ItemAlarmQueue) {
        val intent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
            putExtra("EXTRA_MESSAGE", itemAlarmQueue.message)
            action = "com.minizuure.todoplannereducationedition.services.notification.ALARM"
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            DatetimeAppManager(itemAlarmQueue.time).selectedDetailDatetimeISO.toEpochSecond() * 1000,
            PendingIntent.getBroadcast(
                context,
                itemAlarmQueue.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun cancel(itemAlarmQueue: ItemAlarmQueue) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                itemAlarmQueue.id,
                Intent(context, AlarmBroadcastReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }


}