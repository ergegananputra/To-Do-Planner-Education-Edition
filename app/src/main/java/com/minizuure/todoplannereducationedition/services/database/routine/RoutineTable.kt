package com.minizuure.todoplannereducationedition.services.database.routine

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.minizuure.todoplannereducationedition.dialog_modal.model_interfaces.GlobalMinimumInterface

/**
 * [Last Modified: 8 January 2024]
 *
 *
 * [date_start] & [date_end] is stored in the string format of ISO 8601.
 * for example: "2021-08-01T00:00:00.000+02:00[Europe/Paris]"
 *
 *
 * This is the routineTable class. It is used to store the data of a routine.
 * @param id The id of the routine.
 * @param title The title of the routine.
 * @param description The description of the routine.
 * @param date_start The date the routine starts.
 * @param date_end The date the routine ends.
 * @param isSharedToCommunity to check if user share this routine to community or not.
 * @param communityId to make relation with community.<br>
 *
 */
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
