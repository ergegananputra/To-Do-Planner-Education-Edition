package com.minizuure.todoplannereducationedition.services.database.notes

import androidx.room.Dao
import androidx.room.Query
import com.minizuure.todoplannereducationedition.services.database.BaseIODao

@Dao
interface NotesTaskDao : BaseIODao<NotesTaskTable> {

    @Query("SELECT * FROM notes_task_table")
    suspend fun getAll(): List<NotesTaskTable>

    @Query("SELECT COUNT(*) FROM notes_task_table")
    suspend fun getCount(): Int

    @Query("SELECT * FROM notes_task_table WHERE id = :id")
    suspend fun getById(id: Long): NotesTaskTable?

    @Query("SELECT * FROM notes_task_table WHERE fk_task_id = :fkTaskId")
    suspend fun getByFkTaskId(fkTaskId: Long): List<NotesTaskTable>?

    @Query("SELECT * FROM notes_task_table WHERE fk_task_id = :fkTaskId AND category = :category AND date_iso8601 LIKE :date")
    suspend fun getByFkTaskIdAndCategory(fkTaskId: Long, category: String, date : String): List<NotesTaskTable>?

    @Query("SELECT COUNT(*) FROM notes_task_table WHERE fk_task_id = :fkTaskId AND category = :category AND date_iso8601 LIKE :date")
    suspend fun getCountByFkTaskIdAndCategory(fkTaskId: Long, category: String, date : String): Int

}