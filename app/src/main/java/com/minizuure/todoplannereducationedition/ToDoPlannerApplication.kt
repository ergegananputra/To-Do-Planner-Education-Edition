package com.minizuure.todoplannereducationedition

import android.app.Application
import com.minizuure.todoplannereducationedition.services.database.ApplicationDatabase


/**
 *  Digunakan untuk menginisialisasi aplikasi
 *  seperti viewmodel, repository, notifikasi, dan lain-lain
 */
class ToDoPlannerApplication : Application() {

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