package com.minizuure.todoplannereducationedition.model.base

interface NotesTaskInterface {
    val id : Long
    val fkTaskId : Long
    var dateISO8601 : String
    var category : String
    var description : String
    var updatedAt : String
}