package com.minizuure.todoplannereducationedition.dialog_modal.model

import com.minizuure.todoplannereducationedition.model.base.GlobalMinimumInterface

data class DaysOfWeekImpl(
    override val id: Long,
    override var title: String
) : GlobalMinimumInterface {
    fun getId() : Int = id.toInt()
}