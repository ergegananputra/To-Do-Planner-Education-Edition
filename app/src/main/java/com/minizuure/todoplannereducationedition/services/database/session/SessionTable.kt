package com.minizuure.todoplannereducationedition.services.database.session

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineTable

/**
 * This is the sessionTable class. It is used to store the data of a session.
 * @param id The id of the session.
 * @param title The title of the session.
 * @param time_start The time the session starts.
 * @param time_end The time the session ends.
 * @param selected_days The days the session is active. Stored in boolean format.
 *
 * Note: The boolean format is stored as a string of 7 characters. Each character represents a day of the week.
 * the first represent Sunday, the second Monday, and so on. If the character is 1, the session is active on that day.
 */
@Entity(
    tableName = "session_table",
    foreignKeys = [
        ForeignKey(
            entity = RoutineTable::class,
            parentColumns = ["id"],
            childColumns = ["fk_routine_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SessionTable(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "time_start")
    var time_start: String,

    @ColumnInfo(name = "time_end")
    var time_end: String,

    @ColumnInfo(name = "bool_selected_days")
    var selected_days : String  = "0000000",

    @ColumnInfo(name = "fk_routine_id")
    val fk_routine_id : Int = 0,
)
