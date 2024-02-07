package com.minizuure.todoplannereducationedition

import android.app.Application
import com.minizuure.todoplannereducationedition.services.database.ApplicationDatabase
import com.minizuure.todoplannereducationedition.services.notification.AlarmManagerSingleton
import com.minizuure.todoplannereducationedition.services.notification.AndroidAlarmManager
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
        )
    }

    fun deleteAllOperation() {
        CoroutineScope(Dispatchers.IO).launch {
            appRepository.deleteAllDatabase()
        }
    }

}