package com.minizuure.todoplannereducationedition

import com.minizuure.todoplannereducationedition.services.database.DeleteAllOperation
import com.minizuure.todoplannereducationedition.services.database.notes.NotesTaskDao
import com.minizuure.todoplannereducationedition.services.database.notes.NotesTaskTable
import com.minizuure.todoplannereducationedition.services.database.notes.TodoNoteDao
import com.minizuure.todoplannereducationedition.services.database.notes.TodoNoteTable
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineTable
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineTableDao
import com.minizuure.todoplannereducationedition.services.database.session.SessionTable
import com.minizuure.todoplannereducationedition.services.database.session.SessionTableDao
import com.minizuure.todoplannereducationedition.services.database.task.TaskTable
import com.minizuure.todoplannereducationedition.services.database.task.TaskTableDao
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime

/**
 * AppDatabaseRepository
 * This class is used to access the database.
 * Gunakan perantara viewmodel untuk mengakses repository ini.
 * Viewmodel -> Repository -> Dao
 *
 * View Model ada di dalam package services/database/nama_tabel/%ViewModel.kt
 */
class AppDatabaseRepository(
    private val routineTableDao: RoutineTableDao,
    private val sessionTableDao: SessionTableDao,
    private val taskTableDao: TaskTableDao,
    private val todoNoteTableDao: TodoNoteDao,
    private val notesTaskTableDao: NotesTaskDao,
    private val deleteAllOperation: DeleteAllOperation
) {
    // Delete
    private suspend fun deleteAllTasks() = withContext(Dispatchers.IO) { deleteAllOperation.deleteAllTasks() }
    private suspend fun deleteAllSessions() = withContext(Dispatchers.IO) { deleteAllOperation.deleteAllSessions() }
    private suspend fun deleteAllRoutines() = withContext(Dispatchers.IO) { deleteAllOperation.deleteAllRoutines() }
    suspend fun deleteAllDatabase() = withContext(Dispatchers.IO) {
        deleteAllTasks()
        deleteAllSessions()
        deleteAllRoutines()
    }

    // RoutineTableDao
    suspend fun getAllRoutines() = withContext(Dispatchers.IO) { routineTableDao.getAll() }
    suspend fun getRoutinesCount() = withContext(Dispatchers.IO) { routineTableDao.getCount() }
    suspend fun getRoutineById(id: Long) = withContext(Dispatchers.IO) { routineTableDao.getById(id) }
    suspend fun getPaginatedRoutines(limit: Int, offset: Int = 0, searchQuery: String = "") =
        withContext(Dispatchers.IO) {
            routineTableDao.getPaginated(limit, offset, "%$searchQuery%")
        }
    suspend fun insertRoutine(routineTable: RoutineTable) : Long = withContext(Dispatchers.IO) { routineTableDao.insert(routineTable) }
    suspend fun deleteRoutine(routineTable: RoutineTable) = withContext(Dispatchers.IO) { routineTableDao.delete(routineTable) }
    suspend fun updateRoutine(routineTable: RoutineTable) = withContext(Dispatchers.IO) { routineTableDao.update(routineTable) }


    // SessionTableDao
    suspend fun getAllSessions() = withContext(Dispatchers.IO) { sessionTableDao.getAll() }
    suspend fun getSessionById(id: Long) = withContext(Dispatchers.IO) { sessionTableDao.getById(id) }
    suspend fun getPaginatedSessions(limit: Int, offset: Int = 0, searchQuery: String = "") =
        withContext(Dispatchers.IO) {
            sessionTableDao.getPaginated(limit, offset, "%$searchQuery%")
        }
    suspend fun getByRoutineId(routineId: Long) = withContext(Dispatchers.IO) { sessionTableDao.getByRoutineId(routineId) }
    suspend fun insertSession(sessionTable: SessionTable) = withContext(Dispatchers.IO) { sessionTableDao.insert(sessionTable) }
    suspend fun deleteSession(sessionTable: SessionTable) = withContext(Dispatchers.IO) { sessionTableDao.delete(sessionTable) }
    suspend fun updateSession(sessionTable: SessionTable) = withContext(Dispatchers.IO) { sessionTableDao.update(sessionTable) }
    suspend fun searchSessions(searchQuery: String) = withContext(Dispatchers.IO) { sessionTableDao.search("%$searchQuery%") }
    suspend fun countSessionsForRoutine(routineId: Long) = withContext(Dispatchers.IO) { sessionTableDao.countSessionsForRoutine(routineId) }
    suspend fun getSessionsForRoutine(routineId: Long, isCustomSessionIncluded : Boolean) = withContext(Dispatchers.IO) { sessionTableDao.getSessionsForRoutine(routineId, isCustomSessionIncluded) }


    // TaskTableDao
    suspend fun getAllTasks() = withContext(Dispatchers.IO) { taskTableDao.getAll() }
    suspend fun getTaskById(id: Long) = withContext(Dispatchers.IO) { taskTableDao.getById(id) }
    suspend fun getPaginatedTasks(limit: Int, offset: Int = 0) =
        withContext(Dispatchers.IO) {
            taskTableDao.getPaginated(limit, offset)
        }
    suspend fun getTasksByIndexDay(indexDay: Int, selectedDate : ZonedDateTime) = withContext(Dispatchers.IO) {
        val routines = routineTableDao.getAll()
        val validRoutineIds = routines.filter {
            val endTime = DatetimeAppManager(it.date_end).selectedDetailDatetimeISO
            selectedDate.isBefore(endTime) || selectedDate.isEqual(endTime)
        }.map { it.id }


        taskTableDao.getByIndexDay(indexDay, validRoutineIds)
    }

    suspend fun getTaskAndSessionJoinByIndexDay(indexDay: Int, selectedDate : ZonedDateTime) = withContext(Dispatchers.IO) {
        val routines = routineTableDao.getAll()
        val validRoutineIds = routines.filter {
            val endTime = DatetimeAppManager(it.date_end).selectedDetailDatetimeISO
            selectedDate.isBefore(endTime) || selectedDate.isEqual(endTime)
        }.map { it.id }

        taskTableDao.getTaskAndSessionJoinByIndexDay(indexDay, validRoutineIds)
    }

    suspend fun getTasksBySessionId(sessionId: Long) = withContext(Dispatchers.IO) { taskTableDao.getBySessionId(sessionId) }
    suspend fun insertTask(taskTable: TaskTable) :Long = withContext(Dispatchers.IO) { taskTableDao.insert(taskTable) }
    suspend fun deleteTask(taskTable: TaskTable) = withContext(Dispatchers.IO) { taskTableDao.delete(taskTable) }
    suspend fun updateTask(taskTable: TaskTable) = withContext(Dispatchers.IO) {
        taskTable.updatedAt = DatetimeAppManager().dateISO8601inString
        taskTableDao.update(taskTable)
    }

    // TodoNoteDao
    suspend fun getAllTodoNotes() = withContext(Dispatchers.IO) { todoNoteTableDao.getAll() }
    suspend fun getTodoNoteById(id: Long) = withContext(Dispatchers.IO) { todoNoteTableDao.getById(id) }
    suspend fun getCountTodoNotes() = withContext(Dispatchers.IO) { todoNoteTableDao.getCount() }
    suspend fun getTodoNoteByFkNoteId(fkNoteId: Long) = withContext(Dispatchers.IO) { todoNoteTableDao.getByFkNotesTaskId(fkNoteId) }
    suspend fun insertTodoNotes(todoNoteTable: TodoNoteTable) : Long = withContext(Dispatchers.IO) { todoNoteTableDao.insert(todoNoteTable) }
    suspend fun updateTodoNotes(todoNoteTable: TodoNoteTable) = withContext(Dispatchers.IO) {
        val updateTime = DatetimeAppManager().dateISO8601inString

        todoNoteTable.updatedAt = updateTime
        todoNoteTableDao.update(todoNoteTable)

        val notes = getNotesTaskById(todoNoteTable.fkNotesTaskId) ?: return@withContext
        notes.updatedAt = updateTime
        notesTaskTableDao.update(notes)

        val task = getTaskById(notes.fkTaskId) ?: return@withContext
        task.updatedAt = updateTime
        taskTableDao.update(task)
    }
    suspend fun deleteTodoNotes(todoNoteTable: TodoNoteTable) = withContext(Dispatchers.IO) { todoNoteTableDao.delete(todoNoteTable) }

    // NotesTaskDao
    suspend fun getAllNotesTasks() = withContext(Dispatchers.IO) { notesTaskTableDao.getAll() }
    suspend fun getNotesTaskById(id: Long) = withContext(Dispatchers.IO) { notesTaskTableDao.getById(id) }
    suspend fun getCountNotesTasks() = withContext(Dispatchers.IO) { notesTaskTableDao.getCount() }
    suspend fun getNotesTaskByTaskId(taskId: Long) = withContext(Dispatchers.IO) { notesTaskTableDao.getByFkTaskId(taskId) }
    suspend fun getNotesTaskByTaskIdAndCategory(taskId: Long, category: String, date : String) = withContext(Dispatchers.IO) {
        notesTaskTableDao.getByFkTaskIdAndCategory(taskId, category, "%${date.trim()}%")
    }
    suspend fun getCountNotesTaskByTaskIdAndCategory(taskId: Long, category: String, date : String) = withContext(Dispatchers.IO) {
        notesTaskTableDao.getCountByFkTaskIdAndCategory(taskId, category, "%${date.trim()}%")
    }
    suspend fun insertNotesTask(notesTaskTable: NotesTaskTable) : Long = withContext(Dispatchers.IO) { notesTaskTableDao.insert(notesTaskTable) }
    suspend fun updateNotesTask(notesTaskTable: NotesTaskTable) = withContext(Dispatchers.IO) {
        val updateTime = DatetimeAppManager().dateISO8601inString
        notesTaskTable.updatedAt = updateTime
        notesTaskTableDao.update(notesTaskTable)

        val task = getTaskById(notesTaskTable.fkTaskId) ?: return@withContext
        task.updatedAt = updateTime
        taskTableDao.update(task)
    }
    suspend fun deleteNotesTask(notesTaskTable: NotesTaskTable) = withContext(Dispatchers.IO) { notesTaskTableDao.delete(notesTaskTable) }


}
