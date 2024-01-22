package com.minizuure.todoplannereducationedition.services.database.queue

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager
import com.minizuure.todoplannereducationedition.services.notification.ItemAlarmQueue

/**
 *
 * Gunakan [time] untuk mengatur waktu alarm.
 *
 *
 * [taskDateIdentification] digunakan untuk mencari notes.
 *
 */
@Entity(tableName = "notification_queue_table")
data class NotificationQueueTable (
    @PrimaryKey(autoGenerate = false)
    val id: Long,

    @ColumnInfo(name = "action")
    val action: String,

    @ColumnInfo(name = "taskId")
    val taskId: Long,

    @ColumnInfo(name = "time")
    val time: String,

    @ColumnInfo(name = "taskName")
    val taskName: String,

    @ColumnInfo(name = "message")
    val message: String,

    @ColumnInfo(name = "taskDateIdentification")
    val taskDateIdentification: String,
) {
    companion object {
        fun convertItemToTable(item: ItemAlarmQueue): NotificationQueueTable {
            return NotificationQueueTable(
                id = item.id.toLong(),
                action = item.action,
                taskId = item.taskId,
                time = item.time.toString(),
                taskName = item.taskName,
                message = item.message,
                taskDateIdentification = item.taskDateIdentification.toString(),
            )
        }

        fun convertTableToItem(table: NotificationQueueTable): ItemAlarmQueue {
            return ItemAlarmQueue(
                id = table.id.toInt(),
                action = table.action,
                taskId = table.taskId,
                time = DatetimeAppManager(table.time).selectedDetailDatetimeISO,
                taskName = table.taskName,
                message = table.message,
                taskDateIdentification = DatetimeAppManager(table.taskDateIdentification).selectedDetailDatetimeISO,
            )
        }
    }
}