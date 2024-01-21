package com.minizuure.todoplannereducationedition

import android.app.Application
import com.minizuure.todoplannereducationedition.services.database.ApplicationDatabase
import com.minizuure.todoplannereducationedition.services.notification.AlarmManagerSingleton
import com.minizuure.todoplannereducationedition.services.notification.AndroidAlarmManager


/**
 *  Digunakan untuk menginisialisasi aplikasi
 *  seperti viewmodel, repository, notifikasi, dan lain-lain
 */
class ToDoPlannerApplication : Application() {

    lateinit var appAlarmManager : AndroidAlarmManager

    override fun onCreate() {
        super.onCreate()
        appAlarmManager = AlarmManagerSingleton.getInstance(this).instance
    }



    private val appDb by lazy {
        ApplicationDatabase.getDatabase(this)
    }

    val appRepository by lazy {
        AppDatabaseRepository(
            appDb.routineTableDao(),
            appDb.sessionTableDao(),
            appDb.taskTableDao(),
            appDb.todoNoteTableDao(),
            appDb.notesTaskTableDao(),
            appDb.deleteAllOperation()
        )
    }

}