package com.minizuure.todoplannereducationedition.services.database.session

import androidx.room.Dao
import androidx.room.Query
import com.minizuure.todoplannereducationedition.services.database.BaseIODao

@Dao
interface SessionTableDao : BaseIODao<SessionTable> {
    @Query("SELECT * FROM session_table")
    suspend fun getAll(): List<SessionTable>

    @Query("SELECT * FROM session_table WHERE title LIKE :searchQuery LIMIT :limit OFFSET :offset")
    suspend fun getPaginated(limit: Int, offset: Int = 0, searchQuery: String = ""): List<SessionTable>

    @Query("SELECT * FROM session_table WHERE id = :id")
    suspend fun getById(id: Long): SessionTable?

    @Query("SELECT * FROM session_table WHERE fk_routine_id = :routineId")
    suspend fun getByRoutineId(routineId: Long): List<SessionTable>


    @Query("SELECT * FROM session_table WHERE title LIKE :searchQuery")
    suspend fun search(searchQuery: String): List<SessionTable>

    @Query("SELECT COUNT(*) FROM session_table WHERE fk_routine_id = :routineId")
    suspend fun countSessionsForRoutine(routineId: Long): Int

    @Query("SELECT * FROM session_table WHERE fk_routine_id = :routineId")
    suspend fun getSessionsForRoutine(routineId: Long): List<SessionTable>
}