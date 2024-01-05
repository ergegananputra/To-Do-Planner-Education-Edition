package com.minizuure.todoplannereducationedition.model

data class TempSession(
    val index : Int = 0,
    val title : String,
    val startTime : String,
    val endTime : String,
    val daysSelected : String
)
