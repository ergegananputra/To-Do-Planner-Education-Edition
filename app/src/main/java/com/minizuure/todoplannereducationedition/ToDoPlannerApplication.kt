package com.minizuure.todoplannereducationedition

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.minizuure.todoplannereducationedition.services.database.ApplicationDatabase
import com.minizuure.todoplannereducationedition.services.notification.AlarmManagerSingleton
import com.minizuure.todoplannereducationedition.services.notification.AndroidAlarmManager
import com.minizuure.todoplannereducationedition.services.notification.CHANNEL_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 *  Digunakan untuk menginisialisasi aplikasi
 *  seperti viewmodel, repository, notifikasi, dan lain-lain
 */
class ToDoPlannerApplication : Application() {

    lateinit var appAlarmManager : AndroidAlarmManager

    override fun onCreate() {
        super.onCreate()
        appAlarmManager = AlarmManagerSingleton.getInstance(this).instance
        setNotificationChannel()

    }

    private fun setNotificationChannel() {
        val channelNotification = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.notification_channel_main_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            setShowBadge(true)
            lightColor = getColor(R.color.white)
            enableLights(true)
            description = getString(R.string.notification_channel_main_description)
        }

        val channelServicess = NotificationChannel(
            "TODO_PLANNER_EDUCATION_EDITION_BY_MINIZUURE_SERVICES_NOTIFICATIONS",
            getString(R.string.syncronization_notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = getString(R.string.syncronization_notification_channel_description)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channelNotification)
        notificationManager.createNotificationChannel(channelServicess)
    }



    val appDb by lazy {
        ApplicationDatabase.getDatabase(this)
    }

    val appRepository by lazy {
        AppDatabaseRepository(
            routineTableDao = appDb.routineTableDao(),
            sessionTableDao = appDb.sessionTableDao(),
            taskTableDao = appDb.taskTableDao(),
            todoNoteTableDao = appDb.todoNoteTableDao(),
            notesTaskTableDao = appDb.notesTaskTableDao(),
            sessionTaskProviderTableDao = appDb.sessionTaskProviderTableDao(),
            deleteAllOperation = appDb.deleteAllOperation(),
            reservationTableDao = appDb.reservationTableDao(),
        )
    }

    fun deleteAllOperation() {
        CoroutineScope(Dispatchers.IO).launch {
            appRepository.deleteAllDatabase()
        }
    }

}