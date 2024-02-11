package com.minizuure.todoplannereducationedition.services.database.relations_table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.minizuure.todoplannereducationedition.model.base.SessionTaskProviderInterface
import com.minizuure.todoplannereducationedition.services.database.session.SessionTable
import com.minizuure.todoplannereducationedition.services.database.task.TaskTable

@Entity(
    tableName = "session_task_provider_table",
    primaryKeys = [
        "index_day",
        "fk_task_id",
        "fk_session_id"
                  ],
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
    @ColumnInfo(name = "index_day")
    override val indexDay: Int = 0,

    @ColumnInfo(name = "fk_task_id")
    override val fkTaskId: Long,

    @ColumnInfo(name = "fk_session_id")
    override val fkSessionId: Long,

    @ColumnInfo(name = "is_rescheduled")
    override var isRescheduled : Boolean = false,

    @ColumnInfo(name = "rescheduled_date_start")
    override var rescheduledDateStart : String? = null,

    @ColumnInfo(name = "rescheduled_date_end")
    override var rescheduledDateEnd : String? = null,

    // Additional Location

    @ColumnInfo(name = "location_name")
    override var locationName : String? = null,

    @ColumnInfo(name = "location_link")
    override var locationLink : String? = null,
) : SessionTaskProviderInterface