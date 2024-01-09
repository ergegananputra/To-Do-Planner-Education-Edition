package com.minizuure.todoplannereducationedition.services.database.notes

import androidx.room.Dao
import androidx.room.Query
import com.minizuure.todoplannereducationedition.services.database.BaseIODao

@Dao
interface TodoNoteDao : BaseIODao<TodoNoteTable> {

    @Query("SELECT * FROM todo_note_table")
    suspend fun getAll(): List<TodoNoteTable>

    @Query("SELECT COUNT(*) FROM todo_note_table")
    suspend fun getCount(): Int

    @Query("SELECT * FROM todo_note_table WHERE id = :id")
    suspend fun getById(id: Long): TodoNoteTable?

    @Query("SELECT * FROM todo_note_table WHERE fk_notes_task_id = :fkNotesTaskId")
    suspend fun getByFkNotesTaskId(fkNotesTaskId: Long): List<TodoNoteTable>?

}