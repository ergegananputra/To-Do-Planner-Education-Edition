package com.minizuure.todoplannereducationedition.dialog_modal.model

import com.minizuure.todoplannereducationedition.dialog_modal.model_interfaces.GlobalMinimumInterface

data class DaysOfWeekImpl(
    override val id: Long,
    override var title: String
) : GlobalMinimumInterface