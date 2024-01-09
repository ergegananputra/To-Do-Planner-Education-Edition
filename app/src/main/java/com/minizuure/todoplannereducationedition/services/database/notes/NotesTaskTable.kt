package com.minizuure.todoplannereducationedition.services.database.notes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.minizuure.todoplannereducationedition.services.database.task.TaskTable

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

    @ColumnInfo(name = "category")
    var category : String,

    @ColumnInfo(name = "description")
    var description : String,

    @ColumnInfo(name = "updated_at")
    var updatedAt : Long = System.currentTimeMillis(),
) {
    fun getNewTimestamp() = System.currentTimeMillis()

}
