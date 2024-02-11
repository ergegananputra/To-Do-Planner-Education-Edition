package com.minizuure.todoplannereducationedition.model.base

interface TodoNoteInterface {
    val id : Long
    val fkNotesTaskId : Long
    var isChecked : Boolean
    var description : String
    var updatedAt : String
}