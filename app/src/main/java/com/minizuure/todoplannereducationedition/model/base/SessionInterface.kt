package com.minizuure.todoplannereducationedition.model.base

interface SessionInterface : GlobalMinimumInterface {
    var timeStart: String
    var timeEnd: String
    var selectedDays : String
    val fkRoutineId : Long
    var isCustomSession : Boolean
}