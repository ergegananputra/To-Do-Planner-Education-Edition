package com.minizuure.todoplannereducationedition.services.database.notes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.minizuure.todoplannereducationedition.services.database.task.TaskTable


/**
 * [Last edited by ergegananputra 11 January 2024]
 *
 *
 * The [dateISO8601] accuracy is only up to day.
 *
 *
 * Use zonedDateTime.truncatedTo(ChronoUnit.DAYS) to get the date.
 *
 *
 * @param id The id of the notesTaskTable.
 * @param fkTaskId The id of the taskTable.
 * @param dateISO8601 The date the notesTaskTable is active. Stored in ISO8601 format.
 * @param category The category of the notesTaskTable example: Quiz, To-Pack, Memo.
 * @param description The description of the notesTaskTable.
 * @param updatedAt The time the notesTaskTable is updated. (automatic when use insert or update function)
 *
 */
@Entity(
    tableName = "notes_task_table",
    foreignKeys = [
        ForeignKey(
            entity = TaskTable::class,
            parentColumns = ["id"],
            childColumns = ["fk_task_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("fk_task_id")]
)
data class NotesTaskTable(
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0,

    @ColumnInfo(name = "fk_task_id")
    val fkTaskId : Long,

    @ColumnInfo(name = "date_iso8601")
    var dateISO8601 : String,

    @ColumnInfo(name = "category")
    var category : String,

    @ColumnInfo(name = "description")
    var description : String,

    @ColumnInfo(name = "updated_at")
    var updatedAt : Long = System.currentTimeMillis(),
) {
    fun getNewTimestamp() = System.currentTimeMillis()

}
