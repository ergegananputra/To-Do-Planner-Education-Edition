package com.minizuure.todoplannereducationedition.services.database.task

import androidx.room.Dao
import androidx.room.Query
import com.minizuure.todoplannereducationedition.services.database.BaseIODao

@Dao
interface TaskTableDao : BaseIODao<TaskTable> {

    @Query("SELECT * FROM task_table")
    suspend fun getAll(): List<TaskTable>

    @Query("SELECT * FROM task_table LIMIT :limit OFFSET :offset")
    suspend fun getPaginated(limit: Int, offset: Int = 0): List<TaskTable>


    @Query("SELECT * FROM task_table WHERE id = :id")
    suspend fun getById(id: Long): TaskTable?

    @Query("SELECT * FROM task_table WHERE index_day = :indexDay")
    suspend fun getByIndexDay(indexDay: Int): List<TaskTable>

    @Query("SELECT * FROM task_table WHERE is_custom_session = 1 AND session_id = :sessionId")
    suspend fun getBySessionId(sessionId: Long): List<TaskTable>


}