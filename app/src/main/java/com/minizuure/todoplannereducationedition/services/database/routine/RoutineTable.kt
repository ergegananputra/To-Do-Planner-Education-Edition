package com.minizuure.todoplannereducationedition.services.database.routine

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.minizuure.todoplannereducationedition.dialog_modal.model_interfaces.GlobalMinimumInterface

@Entity(tableName = "routine_table")
data class RoutineTable(
    @PrimaryKey(autoGenerate = true)
    override val id: Long = 0,

    @ColumnInfo(name = "title")
    override var title: String,

    @ColumnInfo(name = "description")
    var description: String,

    @ColumnInfo(name = "date_start")
    var date_start: String,

    @ColumnInfo(name = "date_end")
    var date_end: String,

    @ColumnInfo(name = "isSharedToCommunity")
    var isSharedToCommunity: Boolean = false,

    @ColumnInfo(name = "communityId")
    var communityId: String? = null,
) : GlobalMinimumInterface
