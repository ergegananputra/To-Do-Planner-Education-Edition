package com.minizuure.todoplannereducationedition.services.notification

import java.time.ZonedDateTime

data class ItemAlarmQueue(
    val id : Int,
    val action : String,
    val time : ZonedDateTime,
    val message : String,
    val monthCreated : Int,
)
