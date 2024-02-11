package com.minizuure.todoplannereducationedition.services.database.session

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.minizuure.todoplannereducationedition.model.base.SessionInterface
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineTable

/**
 * This is the sessionTable class. It is used to store the data of a session.
 * @param id The id of the session.
 * @param title The title of the session.
 * @param time_start The time the session starts.
 * @param time_end The time the session ends.
 * @param selected_days The days the session is active. Stored in boolean format.
 * @param fkRoutineId The id of the routine the session belongs to.
 * @param isCustomSession to check if user choose custom session or not.
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
    ],
    indices = [Index("fk_routine_id")]
)
data class SessionTable(
    @PrimaryKey(autoGenerate = true)
    override val id: Long = 0,

    @ColumnInfo(name = "title")
    override var title: String,

    @ColumnInfo(name = "time_start")
    override var timeStart: String,

    @ColumnInfo(name = "time_end")
    override var timeEnd: String,

    @ColumnInfo(name = "bool_selected_days")
    override var selectedDays : String  = "0000000",

    @ColumnInfo(name = "fk_routine_id")
    override val fkRoutineId : Long = 0,

    @ColumnInfo(name = "is_custom_session")
    override var isCustomSession : Boolean = false,

    ) : SessionInterface
