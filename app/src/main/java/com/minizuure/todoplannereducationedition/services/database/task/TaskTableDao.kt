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
                session_table.is_custom_session AS is_custom_session,
                task_table.is_rescheduled AS is_rescheduled,
                task_table.rescheduled_time_start AS rescheduled_time_start,
                task_table.rescheduled_time_end AS rescheduled_time_end
        FROM task_table
        JOIN session_table ON task_table.session_id = session_table.id
        JOIN routine_table ON session_table.fk_routine_id =  routine_table.id
        WHERE task_table.index_day = :indexDay 
        AND :iso8601Date BETWEEN routine_table.date_start AND routine_table.date_end
        AND (
            task_table.is_rescheduled = 0 
            OR (
                task_table.is_rescheduled = 1 
                AND :iso8601Date BETWEEN task_table.rescheduled_time_start AND task_table.rescheduled_time_end
            )
        )
        ORDER BY 
            CASE WHEN :isToday = 1 
                THEN CASE WHEN :todayHour <= session_table.time_end 
                        THEN 0
                        ELSE 1
                    END
                ELSE session_table.time_end
            END ASC
        
        """
    )
    suspend fun getTaskAndSessionJoinByIndexDay(
        indexDay: Int,
        iso8601Date : String,
        isToday : Boolean,
        todayHour : String
    ): List<TaskAndSessionJoin>
}