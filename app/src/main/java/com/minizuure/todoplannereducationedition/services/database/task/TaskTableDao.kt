package com.minizuure.todoplannereducationedition.services.database.task

import androidx.room.Dao
import androidx.room.Query
import com.minizuure.todoplannereducationedition.services.database.BaseIODao
import com.minizuure.todoplannereducationedition.services.database.join.TaskAndSessionJoin

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


    @Query("""
        SELECT 
                task_table.id AS id,
                task_table.title AS title,
                task_table.index_day AS index_day,
                task_table.session_id AS session_id,
                task_table.updated_at AS updated_at,
                task_table.location_name AS location_name,
                task_table.location_link AS location_link,
                task_table.is_shared_to_community AS is_shared_to_community,
                task_table.community_id AS community_id,
                session_table.title AS session_title, 
                session_table.time_start AS time_start, 
                session_table.time_end AS time_end,
                session_table.bool_selected_days AS bool_selected_days, 
                session_table.fk_routine_id AS fk_routine_id, 
                session_table.is_custom_session AS is_custom_session
        FROM task_table
        JOIN session_table ON task_table.session_id = session_table.id
        JOIN routine_table ON session_table.fk_routine_id =  routine_table.id
        WHERE task_table.index_day = :indexDay AND routine_table.id IN (:fkRoutinesIds)
        """
    )
    suspend fun getTaskAndSessionJoinByIndexDay(indexDay: Int, fkRoutinesIds: List<Long>): List<TaskAndSessionJoin>
}