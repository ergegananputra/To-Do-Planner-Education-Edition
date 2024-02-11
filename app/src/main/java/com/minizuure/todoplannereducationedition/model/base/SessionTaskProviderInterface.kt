package com.minizuure.todoplannereducationedition.model.base

interface SessionTaskProviderInterface {
    val indexDay: Int
    val fkTaskId: Long
    val fkSessionId: Long
    var isRescheduled : Boolean
    var rescheduledDateStart : String?
    var rescheduledDateEnd : String?
    var locationName : String?
    var locationLink : String?
}