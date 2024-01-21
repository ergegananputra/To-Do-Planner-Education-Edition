package com.minizuure.todoplannereducationedition.services.notification

import android.content.Context

class AlarmManagerSingleton private constructor(
    private val context: Context
) {
    val instance : AndroidAlarmManager by lazy {
        AndroidAlarmManager(context)
    }

    companion object {
        @Volatile
        private var INSTANCE: AlarmManagerSingleton? = null

        fun getInstance(context: Context): AlarmManagerSingleton {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AlarmManagerSingleton(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }
}