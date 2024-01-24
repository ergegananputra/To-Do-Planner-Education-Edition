package com.minizuure.todoplannereducationedition.services.database.task

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager

/**
 * [Last edited by ergegananputra 23 January 2024]
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
 * [isRescheduled] digunakan untuk mengecek apakah Task telah dilakukan reschedule atau tidak.
 * Reschedule ini akan membuat duplikat 1 hingga 2 kali dari task yang telah dibuat.
 * Oleh karena itu, ketika melakukan duplikasi, pastikan notes juga ikut dipindahkan.
 *
 *
 * Proses duplikasi akan seperti berikut:
 * 1. Copy data Task
 * 2. Buat data Task duplikat
 * 3. Buat data Task duplikat (jika hanya 1 kali reschedule)
 * 4. Update Task asli pada kolom [isRescheduled] menjadi true, [rescheduledTimeEnd] menjadi waktu reschedule
 * 5. Update Task duplikat pada kolom [isRescheduled] menjadi true, [rescheduledTimeStart] menjadi waktu reschedule, dan [rescheduledTimeEnd] menjadi waktu reschedule
 * 6. Copy data NotesTask
 * 7. Hapus data NotesTask asli setelah waktu reschedule
 *
 *
 * This is the taskTable class. It is used to store the data of a task.
 * @param id The id of the task.
 * @param title The title of the task.
 * @param indexDay The day the task is active. Stored in integer format.
 * @param sessionId to make relation with session, it's not must because sometimes user choose custom session.
 * @param updatedAt The time the task is updated.
 * @param locationName The name of the location.
 * @param locationAddress The address of the location.
 * @param isRescheduled to check if user reschedule this task or not.
 * @param rescheduledTimeStart The time the task starts.
 * @param rescheduledTimeEnd The time the task ends.
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

//    @ColumnInfo(name = "session_id")
//    var sessionId : Long = DEFAULT_SESSION_ID,

    @ColumnInfo(name = "updated_at")
    var updatedAt: String = DatetimeAppManager().dateISO8601inString,

    // Additional Location

    @ColumnInfo(name = "location_name")
    var locationName : String? = null,

    @ColumnInfo(name = "location_link")
    var locationAddress : String? = null,

//
//    // custom reschedule
//    @ColumnInfo(name = "is_rescheduled")
//    var isRescheduled : Boolean = false,
//
//    @ColumnInfo(name = "rescheduled_time_start")
//    var rescheduledTimeStart : String? = null,
//
//    @ColumnInfo(name = "rescheduled_time_end")
//    var rescheduledTimeEnd : String? = null,


    // Community Feature

    @ColumnInfo(name = "is_shared_to_community")
    var isSharedToCommunity : Boolean = false,

    @ColumnInfo(name = "community_id")
    var communityId : String? = null,

    )
