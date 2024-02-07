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

    @Query("DELETE FROM todo_note_table")
    suspend fun deleteAllTodoNotes()

    @Query("DELETE FROM notes_task_table")
    suspend fun deleteAllNotesTasks()

    @Query("DELETE FROM notification_queue_table")
    suspend fun deleteAllNotificationQueue()

    @Query("DELETE FROM session_task_provider_table")
    suspend fun deleteAllSessionTaskProvider()


}