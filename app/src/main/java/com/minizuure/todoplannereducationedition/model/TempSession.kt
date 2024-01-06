package com.minizuure.todoplannereducationedition.model

data class TempSession(
    val id : Long = 0L,
    val title : String,
    val startTime : String,
    val endTime : String,
    val daysSelected : String
)
