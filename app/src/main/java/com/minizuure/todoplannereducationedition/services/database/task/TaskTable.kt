package com.minizuure.todoplannereducationedition.services.database.task

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.minizuure.todoplannereducationedition.services.database.CUSTOM_SESSION_ID

/**
 * [Last edited by ergegananputra 11 January 2024]
 *
 *
 * Kelas ini tidak menyimpan Memo, Quiz, dan To-Pack. Kelas ini hanya menyimpan data dari task yang dibuat oleh user.
 * Memo, Quiz, dan To-Pack disimpan di kelas [NotesTaskTable].
 *
 *
 * Maka dari itu untuk mengambil data Memo, Quiz, dan To-Pack, kita harus mengambil data dari
 * kelas [NotesTaskTable] dengan menggunakan id dari task yang dibuat oleh user.
 *
 *
 * NOTE: The startTime and endTime must be updated when the user changes the time of the session.
 *
 *
 * This is the taskTable class. It is used to store the data of a task.
 * @param id The id of the task.
 * @param title The title of the task.
 * @param indexDay The day the task is active. Stored in integer format.
 * @param sessionId to make relation with session, it's not must because sometimes user choose custom session.
 * @param updatedAt The time the task is updated.
 * @param isCustomSession to check if user choose custom session or not.
 * @param startTime The time the task starts.
 * @param endTime The time the task ends.
 * @param locationName The name of the location.
 * @param locationAddress The address of the location.
 * @param isSharedToCommunity to check if user share this task to community or not.
 * @param communityId to make relation with community.<br>
 *
 *
 */
@Entity(tableName = "task_table")
data class TaskTable(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "index_day")
    var indexDay : Int = 0,

    @ColumnInfo(name = "session_id")
    var sessionId : Long = CUSTOM_SESSION_ID,

    @ColumnInfo(name = "updated_at")
    var updatedAt: Long = System.currentTimeMillis(),

    // Additional Custom Session

    @ColumnInfo(name = "is_custom_session")
    var isCustomSession : Boolean = false,

    @ColumnInfo(name = "time_start")
    var startTime : String? = null,

    @ColumnInfo(name = "time_end")
    var endTime : String? = null,

    // Additional Location

    @ColumnInfo(name = "location_name")
    var locationName : String? = null,

    @ColumnInfo(name = "location_link")
    var locationAddress : String? = null,


    // Community Feature

    @ColumnInfo(name = "is_shared_to_community")
    var isSharedToCommunity : Boolean = false,

    @ColumnInfo(name = "community_id")
    var communityId : String? = null,

)
