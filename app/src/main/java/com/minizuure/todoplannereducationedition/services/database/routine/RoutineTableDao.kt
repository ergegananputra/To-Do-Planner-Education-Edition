package com.minizuure.todoplannereducationedition.services.database.routine

import androidx.room.Dao
import androidx.room.Query
import com.minizuure.todoplannereducationedition.services.database.BaseIODao


@Dao
interface RoutineTableDao : BaseIODao<RoutineTable> {

    @Query("SELECT * FROM routine_table")
    suspend fun getAll(): List<RoutineTable>

    @Query("SELECT * FROM routine_table WHERE id = :id")
    suspend fun getById(id: Long): RoutineTable?

    @Query("SELECT * FROM routine_table WHERE title LIKE :searchQuery LIMIT :limit OFFSET :offset")
    suspend fun getPaginated(limit: Int, offset: Int = 0, searchQuery: String = ""): List<RoutineTable>


}