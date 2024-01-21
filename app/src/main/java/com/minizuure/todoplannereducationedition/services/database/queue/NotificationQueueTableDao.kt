package com.minizuure.todoplannereducationedition.services.database.queue

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NotificationQueueTableDao {

    // get all
    @Query("SELECT * FROM notification_queue_table")
    suspend fun getAll(): List<NotificationQueueTable>

    // insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(obj: NotificationQueueTable) : Long

    // delete by id
    @Query("DELETE FROM notification_queue_table WHERE id = :id")
    suspend fun deleteById(id: Long)

    // delete by task id
    @Query("DELETE FROM notification_queue_table WHERE taskId = :taskId")
    suspend fun deleteByTaskId(taskId: Long)

    // delete all
    @Query("DELETE FROM notification_queue_table")
    suspend fun deleteAll()

}