package com.minizuure.todoplannereducationedition.services.database.notes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.minizuure.todoplannereducationedition.services.database.TimeDatabaseAbstraction
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager

@Entity(
    tableName = "todo_note_table",
    foreignKeys = [
        ForeignKey(
            entity = NotesTaskTable::class,
            parentColumns = ["id"],
            childColumns = ["fk_notes_task_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("fk_notes_task_id")]
)
data class TodoNoteTable(
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0,

    @ColumnInfo(name = "fk_notes_task_id")
    val fkNotesTaskId : Long,

    @ColumnInfo(name = "is_checked")
    var isChecked : Boolean = false,

    @ColumnInfo(name = "description")
    var description : String,

    @ColumnInfo(name = "updated_at")
    var updatedAt : String = DatetimeAppManager().dateISO8601inString,
) : TimeDatabaseAbstraction()
