package com.minizuure.todoplannereducationedition.services.notification

import java.time.ZonedDateTime

data class ItemAlarmQueue(
    val id : Int,
    val action : String,
    val taskId : Long,
    val time : ZonedDateTime,
    val taskName : String,
    val message : String,
    val taskDateIdentification : ZonedDateTime,
)
