package com.minizuure.todoplannereducationedition.services.database

import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager

abstract class TimeDatabaseAbstraction {
    fun getNewTimestamp() : String = DatetimeAppManager().dateISO8601inString

}