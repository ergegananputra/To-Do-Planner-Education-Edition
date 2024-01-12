package com.minizuure.todoplannereducationedition.services.notification
interface AlarmScheduler {
    fun schedule(itemAlarmQueue: ItemAlarmQueue)
    fun cancel(itemAlarmQueue: ItemAlarmQueue)
}