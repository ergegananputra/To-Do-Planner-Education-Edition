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

    @Query("""
        SELECT task_table.* 
        FROM task_table 
        JOIN session_table ON task_table.session_id = session_table.id
        JOIN routine_table ON session_table.fk_routine_id =  routine_table.id
        WHERE task_table.index_day = :indexDay AND routine_table.id IN (:fkRoutinesIds)
        """
    )
    suspend fun getByIndexDay(indexDay: Int, fkRoutinesIds: List<Long>): List<TaskTable>

    @Query("""
        SELECT * 
        FROM task_table 
        WHERE session_id = :sessionId
        """
    )
    suspend fun getBySessionId(sessionId: Long): List<TaskTable>


}