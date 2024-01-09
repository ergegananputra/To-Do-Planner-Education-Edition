package com.minizuure.todoplannereducationedition.services.database.task

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Penjelasan dan Tata Cara Penggunaan Class TaskTable terutama [dateISO8601].
 *
 *
 * Penjelasan [dateISO8601]
 *
 *
 * Pada pembuatan [dateISO8601] adalah null, hal ini diperuntukkan untuk menghemat memori.
 * Kolom [dateISO8601] harus diisi ketika user menambahkan NoteTask atau TodoTask untuk membedakan
 * Task yang repetitif identik dan Task yang sudah memiliki NoteTask atau TodoTask.
 *
 *
 * Pengmabilan data Task pada *HomeFragment*, *TodoFragment*, dan management lainnya yang berhubungan
 * dengan task dilakukan dengan cara mencari task berdasarkan [indexDay] dan [dateISO8601].
 * Jika [dateISO8601] null, maka pencarian task hanya berdasarkan [indexDay] saja.
 *
 *
 * Set value [dateISO8601]
 *
 *
 * Untuk mengisi [dateISO8601], pastikan user membuka Task pada date yang bersangkutan.
 *
 *
 * Sebagai contoh di *TodoFragment.kt* :
 *
 *
 * Ketika user mengarahkan tab ke tanggal 19 Januari 2023 dan membuka Task,
 * maka sebelum membuka Task, pastikan telah  mendapatkan tanggal 19 Januari 2023 tersebut.
 * Dengan demikian [dateISO8601] dapat diisi dengan tanggal 19 Januari 2023 dengan FORMAT ISO8601.
 * Gunakan DatetimeAppManager untuk melakukan konversi!
 *
 *
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
 * @param dateISO8601 The date the task is active. Stored in ISO8601 format. NOTE: Filled when there is NoteTask or TodoTask.
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
    var sessionId : Long = 0,

    @ColumnInfo(name = "date_iso8601")
    var dateISO8601 : String? = null,

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
