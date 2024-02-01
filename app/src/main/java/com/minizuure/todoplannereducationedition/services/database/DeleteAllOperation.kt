package com.minizuure.todoplannereducationedition.services.database

import androidx.room.Dao
import androidx.room.Query

@Dao
interface DeleteAllOperation {

    @Query("DELETE FROM task_table")
    suspend fun deleteAllTasks()

    @Query("DELETE FROM session_table")
    suspend fun deleteAllSessions()

    @Query("DELETE FROM routine_table")
    suspend fun deleteAllRoutines()


}