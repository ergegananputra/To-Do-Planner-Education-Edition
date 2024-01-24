package com.minizuure.todoplannereducationedition.services.database.relations_table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.minizuure.todoplannereducationedition.services.database.session.SessionTable
import com.minizuure.todoplannereducationedition.services.database.task.TaskTable

@Entity(
    tableName = "session_task_provider_table",
    foreignKeys = [
        ForeignKey(
            entity = TaskTable::class,
            parentColumns = ["id"],
            childColumns = ["fk_task_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SessionTable::class,
            parentColumns = ["id"],
            childColumns = ["fk_session_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("fk_task_id"),
        Index("fk_session_id")
    ]
)
data class SessionTaskProviderTable (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "fk_task_id")
    val fkTaskId: Long,

    @ColumnInfo(name = "fk_session_id")
    val fkSessionId: Long,

    @ColumnInfo(name = "is_rescheduled")
    var isRescheduled : Boolean = false,

    @ColumnInfo(name = "rescheduled_time_start")
    var rescheduledTimeStart : String? = null,

    @ColumnInfo(name = "rescheduled_time_end")
    var rescheduledTimeEnd : String? = null,
)