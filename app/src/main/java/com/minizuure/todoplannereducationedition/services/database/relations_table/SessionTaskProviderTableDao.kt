package com.minizuure.todoplannereducationedition.services.database.relations_table

import androidx.room.Dao
import androidx.room.Query
import com.minizuure.todoplannereducationedition.services.database.BaseIODao
import com.minizuure.todoplannereducationedition.services.database.join.TaskAndSessionJoin

@Dao
interface SessionTaskProviderTableDao : BaseIODao<SessionTaskProviderTable> {

    @Query("SELECT * FROM session_task_provider_table")
    suspend fun getAll(): List<SessionTaskProviderTable>

    @Query("SELECT * FROM session_task_provider_table LIMIT :limit OFFSET :offset")
    suspend fun getPaginated(limit: Int, offset: Int = 0): List<SessionTaskProviderTable>

    @Query("SELECT * FROM session_task_provider_table WHERE fk_task_id = :taskId")
    suspend fun getByTaskId(taskId: Long): List<SessionTaskProviderTable>

    @Query("SELECT * FROM session_task_provider_table WHERE index_day = :indexDay AND fk_task_id = :taskId AND fk_session_id = :sessionId")
    suspend fun getByPrimaryKeys(
        indexDay: Int,
        taskId: Long,
        sessionId: Long
    ): SessionTaskProviderTable?

    @Query("DELETE FROM session_task_provider_table WHERE index_day = :indexDay AND fk_task_id = :taskId AND fk_session_id = :sessionId")
    suspend fun deleteByIndexDayAndTaskIdAndSessionId(
        indexDay: Int,
        taskId: Long,
        sessionId: Long
    )

    @Query("""
        UPDATE session_task_provider_table
        SET location_name = :location, location_link = :locationLink
        WHERE index_day = :indexDay AND fk_task_id = :taskId AND fk_session_id = :sessionId
        """
    )
    suspend fun updateLocationByPrimaryKeys(
        indexDay: Int,
        taskId: Long,
        sessionId: Long,
        location: String,
        locationLink : String
    )

    @Query("""
        SELECT 
                task_table.id AS id,
                task_table.title AS title,
                task_table.updated_at AS updated_at,
                task_table.is_shared_to_community AS is_shared_to_community,
                task_table.community_id AS community_id,
                
                session_table.title AS session_title, 
                session_table.time_start AS time_start, 
                session_table.time_end AS time_end,
                session_table.bool_selected_days AS bool_selected_days, 
                session_table.fk_routine_id AS fk_routine_id, 
                session_table.is_custom_session AS is_custom_session,
                
                provider_table.index_day AS index_day,
                provider_table.fk_session_id AS session_id,
                provider_table.is_rescheduled AS is_rescheduled,
                provider_table.rescheduled_date_start AS rescheduled_time_start,
                provider_table.rescheduled_date_end AS rescheduled_time_end,
                provider_table.location_name AS location_name,
                provider_table.location_link AS location_link,
                
                :iso8601Date AS params_selected_iso8601_date
                
        FROM session_task_provider_table as provider_table
        JOIN task_table ON provider_table.fk_task_id = task_table.id
        JOIN session_table ON provider_table.fk_session_id = session_table.id
        JOIN routine_table ON session_table.fk_routine_id =  routine_table.id
        WHERE
            provider_table.index_day = :indexDay
            AND provider_table.fk_task_id = :taskId
            AND provider_table.fk_session_id = :sessionId
        """
    )
    suspend fun getTaskAndSessionJoinByProviderPrimaryKeys(
        indexDay: Int,
        taskId: Long,
        sessionId: Long,
        iso8601Date : String
    ): TaskAndSessionJoin?


    @Query("""
        SELECT DISTINCT COUNT(*) 
        FROM session_task_provider_table as provider_table
        JOIN session_table ON provider_table.fk_task_id = session_table.id
        WHERE
            provider_table.index_day = :indexDay
            AND fk_routine_id = :routineId
    """)
    suspend fun countByIndexDayAndRoutineId(
        indexDay: Int,
        routineId: Long
    ): Int

    @Query("""
        SELECT DISTINCT COUNT(*) 
        FROM session_task_provider_table as provider_table
        JOIN session_table ON provider_table.fk_task_id = session_table.id
        WHERE
            fk_routine_id = :routineId
    """)
    suspend fun countByRoutineId(
        routineId: Long
    ): Int
}