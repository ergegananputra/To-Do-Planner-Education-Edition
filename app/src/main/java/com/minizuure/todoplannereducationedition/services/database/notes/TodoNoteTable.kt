package com.minizuure.todoplannereducationedition.services.database.notes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
    var updatedAt : Long = System.currentTimeMillis(),
) {
    fun getNewTimestamp() = System.currentTimeMillis()

}
