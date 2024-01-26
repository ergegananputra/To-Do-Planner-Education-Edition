package com.minizuure.todoplannereducationedition

import android.util.Log
import com.minizuure.todoplannereducationedition.services.database.DeleteAllOperation
import com.minizuure.todoplannereducationedition.services.database.notes.NotesTaskDao
import com.minizuure.todoplannereducationedition.services.database.notes.NotesTaskTable
import com.minizuure.todoplannereducationedition.services.database.notes.TodoNoteDao
import com.minizuure.todoplannereducationedition.services.database.notes.TodoNoteTable
import com.minizuure.todoplannereducationedition.services.database.relations_table.SessionTaskProviderTable
import com.minizuure.todoplannereducationedition.services.database.relations_table.SessionTaskProviderTableDao
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineTable
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineTableDao
import com.minizuure.todoplannereducationedition.services.database.session.SessionTable
import com.minizuure.todoplannereducationedition.services.database.session.SessionTableDao
import com.minizuure.todoplannereducationedition.services.database.task.TaskTable
import com.minizuure.todoplannereducationedition.services.database.task.TaskTableDao
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

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
    private val sessionTaskProviderTableDao: SessionTaskProviderTableDao,
    private val deleteAllOperation: DeleteAllOperation,
) {
    // Delete
    private suspend fun deleteAllTasks() = withContext(Dispatchers.IO) {
        Log.w("AppDatabaseRepository", "Delete All Tasks in progress")
        deleteAllOperation.deleteAllTasks()
    }
    private suspend fun deleteAllSessions() = withContext(Dispatchers.IO) {
        Log.w("AppDatabaseRepository", "Delete All Sessions in progress")
        deleteAllOperation.deleteAllSessions()
    }
    private suspend fun deleteAllRoutines() = withContext(Dispatchers.IO) {
        Log.w("AppDatabaseRepository", "Delete All Routines in progress")
        deleteAllOperation.deleteAllRoutines()
    }
    suspend fun deleteAllDatabase() = withContext(Dispatchers.IO) {
        deleteAllTasks()
        deleteAllSessions()
        deleteAllRoutines()
    }

    // RoutineTableDao
    suspend fun getAllRoutines() = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "getAllRoutines triggered")
        routineTableDao.getAll()
    }
    suspend fun getRoutinesCount() = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "getRoutinesCount triggered")
        routineTableDao.getCount()
    }
    suspend fun getRoutineById(id: Long) = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "getRoutineById triggered with id $id")
        routineTableDao.getById(id)
    }
    suspend fun getPaginatedRoutines(limit: Int, offset: Int = 0, searchQuery: String = "") =
        withContext(Dispatchers.IO) {
            Log.d("AppDatabaseRepository", "getPaginatedRoutines triggered with limit $limit, offset $offset, searchQuery $searchQuery")
            routineTableDao.getPaginated(limit, offset, "%$searchQuery%")
        }
    suspend fun insertRoutine(routineTable: RoutineTable) : Long = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "insertRoutine triggered with $routineTable")
        routineTableDao.insert(routineTable)
    }
    suspend fun deleteRoutine(routineTable: RoutineTable) = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "deleteRoutine triggered with $routineTable")
        routineTableDao.delete(routineTable)
    }
    suspend fun updateRoutine(routineTable: RoutineTable) = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "updateRoutine triggered with $routineTable")
        routineTableDao.update(routineTable)
    }


    // SessionTableDao
    suspend fun getAllSessions() = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "getAllSessions triggered")
        sessionTableDao.getAll()
    }
    suspend fun getSessionById(id: Long) = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "getSessionById triggered with id $id")
        sessionTableDao.getById(id)
    }
    suspend fun getPaginatedSessions(limit: Int, offset: Int = 0, searchQuery: String = "") =
        withContext(Dispatchers.IO) {
            Log.d("AppDatabaseRepository", "getPaginatedSessions triggered with limit $limit, offset $offset, searchQuery $searchQuery")
            sessionTableDao.getPaginated(limit, offset, "%$searchQuery%")
        }
    suspend fun getByRoutineId(routineId: Long) = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "getByRoutineId triggered with routineId $routineId")
        sessionTableDao.getByRoutineId(routineId)
    }
    suspend fun insertSession(sessionTable: SessionTable) = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "insertSession triggered with $sessionTable")
        sessionTableDao.insert(sessionTable)
    }
    suspend fun deleteSession(sessionTable: SessionTable) = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "deleteSession triggered with $sessionTable")
        sessionTableDao.delete(sessionTable)
    }
    suspend fun updateSession(sessionTable: SessionTable) = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "updateSession triggered with $sessionTable")
        sessionTableDao.update(sessionTable)
    }
    suspend fun searchSessions(searchQuery: String) = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "searchSessions triggered with $searchQuery")
        sessionTableDao.search("%$searchQuery%")
    }
    suspend fun countSessionsForRoutine(routineId: Long) = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "countSessionsForRoutine triggered with routineId $routineId")
        sessionTableDao.countSessionsForRoutine(routineId)
    }
    suspend fun getSessionsForRoutine(routineId: Long, isCustomSessionIncluded : Boolean) = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "getSessionsForRoutine triggered with routineId $routineId, isCustomSessionIncluded $isCustomSessionIncluded")
        sessionTableDao.getSessionsForRoutine(routineId, isCustomSessionIncluded)
    }


    // TaskTableDao
    suspend fun getAllTasks() = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "getAllTasks triggered")
        taskTableDao.getAll()
    }
    suspend fun getTaskById(id: Long) = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "getTaskById triggered with id $id")
        taskTableDao.getById(id)
    }
    suspend fun getPaginatedTasks(limit: Int, offset: Int = 0) =
        withContext(Dispatchers.IO) {
            Log.d("AppDatabaseRepository", "getPaginatedTasks triggered with limit $limit, offset $offset")
            taskTableDao.getPaginated(limit, offset)
        }

    suspend fun getTaskAndSessionJoinByIndexDay(indexDay: Int, selectedDate : ZonedDateTime, isToday : Boolean, todayHour: String) = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "getTaskAndSessionJoinByIndexDay: $indexDay, ${
            selectedDate.withZoneSameInstant(
                ZoneId.of("Z")
            ).format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
        }, $isToday, $todayHour")
        if (todayHour.length != 5) throw Exception("todayHour must be in format HH:mm\n\n")
        val (a,b) = todayHour.split(":")
        if (a.toInt() > 23 || a.toInt() < 0 || b.toInt() > 59 || b.toInt() < 0) throw Exception("Invalid todayHour format : '$todayHour'?\n\n")
        taskTableDao.getTaskAndSessionJoinByIndexDay(
            indexDay,
            DatetimeAppManager(selectedDate, true).dateISO8601inString,
            isToday,
            todayHour
        )
    }

    suspend fun getTasksBySessionId(sessionId: Long) = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "getTasksBySessionId triggered with sessionId $sessionId")
        taskTableDao.getBySessionId(sessionId)
    }
    suspend fun insertTask(taskTable: TaskTable) :Long = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "insertTask triggered with $taskTable")
        taskTableDao.insert(taskTable)
    }
    suspend fun deleteTask(taskTable: TaskTable) = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "deleteTask triggered with $taskTable")
        taskTableDao.delete(taskTable)
    }
    suspend fun updateTask(taskTable: TaskTable) = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "updateTask triggered with $taskTable")
        taskTable.updatedAt = DatetimeAppManager().dateISO8601inString
        taskTableDao.update(taskTable)
    }

    // TodoNoteDao
    suspend fun getAllTodoNotes() = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "getAllTodoNotes triggered")
        todoNoteTableDao.getAll()
    }
    suspend fun getTodoNoteById(id: Long) = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "getTodoNoteById triggered with id $id")
        todoNoteTableDao.getById(id)
    }
    suspend fun getCountTodoNotes() = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "getCountTodoNotes triggered")
        todoNoteTableDao.getCount()
    }
    suspend fun getTodoNoteByFkNoteId(fkNoteId: Long) = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "getTodoNoteByFkNoteId triggered with fkNoteId $fkNoteId")
        todoNoteTableDao.getByFkNotesTaskId(fkNoteId)
    }
    suspend fun insertTodoNotes(todoNoteTable: TodoNoteTable) : Long = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "insertTodoNotes triggered with $todoNoteTable")
        todoNoteTableDao.insert(todoNoteTable)
    }
    suspend fun updateTodoNotes(todoNoteTable: TodoNoteTable) = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "updateTodoNotes triggered with $todoNoteTable")
        val updateTime = DatetimeAppManager().dateISO8601inString

        todoNoteTable.updatedAt = updateTime
        todoNoteTableDao.update(todoNoteTable)

        val notes = getNotesTaskById(todoNoteTable.fkNotesTaskId) ?: return@withContext
        notes.updatedAt = updateTime
        notesTaskTableDao.update(notes)

        val task = getTaskById(notes.fkTaskId) ?: return@withContext
        task.updatedAt = updateTime
        taskTableDao.update(task)

        Log.v("AppDatabaseRepository", "updateTodoNotes: $todoNoteTable")
    }
    suspend fun deleteTodoNotes(todoNoteTable: TodoNoteTable) = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "deleteTodoNotes triggered with $todoNoteTable")
        todoNoteTableDao.delete(todoNoteTable)
    }

    // NotesTaskDao
    suspend fun getAllNotesTasks() = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "getAllNotesTasks triggered")
        notesTaskTableDao.getAll()
    }
    suspend fun getNotesTaskById(id: Long) = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "getNotesTaskById triggered with id $id")
        notesTaskTableDao.getById(id)
    }
    suspend fun getCountNotesTasks() = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "getCountNotesTasks triggered")
        notesTaskTableDao.getCount()
    }
    suspend fun getNotesTaskByTaskId(taskId: Long) = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "getNotesTaskByTaskId triggered with taskId $taskId")
        notesTaskTableDao.getByFkTaskId(taskId)
    }
    suspend fun getNotesTaskByTaskIdAndCategory(taskId: Long, category: String, date : String) = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "getNotesTaskByTaskIdAndCategory triggered with taskId $taskId, category $category, date $date")
        notesTaskTableDao.getByFkTaskIdAndCategory(taskId, category, "%${date.trim()}%")
    }
    suspend fun getCountNotesTaskByTaskIdAndCategory(taskId: Long, category: String, date : String) = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "getCountNotesTaskByTaskIdAndCategory triggered with taskId $taskId, category $category, date $date")
        notesTaskTableDao.getCountByFkTaskIdAndCategory(taskId, category, "%${date.trim()}%")
    }
    suspend fun insertNotesTask(notesTaskTable: NotesTaskTable) : Long = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "insertNotesTask triggered with $notesTaskTable")
        notesTaskTableDao.insert(notesTaskTable)
    }
    suspend fun updateNotesTask(notesTaskTable: NotesTaskTable) = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "updateNotesTask triggered with $notesTaskTable")
        val updateTime = DatetimeAppManager().dateISO8601inString
        notesTaskTable.updatedAt = updateTime
        notesTaskTableDao.update(notesTaskTable)

        val task = getTaskById(notesTaskTable.fkTaskId) ?: return@withContext
        task.updatedAt = updateTime
        taskTableDao.update(task)

        Log.v("AppDatabaseRepository", "updateNotesTask: $notesTaskTable")
    }
    suspend fun deleteNotesTask(notesTaskTable: NotesTaskTable) = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "deleteNotesTask triggered with $notesTaskTable")
        notesTaskTableDao.delete(notesTaskTable)
    }


    // SessionTaskProviderTableDao
    suspend fun getAllSessionTaskProviderTable() = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "getAllSessionTaskProviderTable triggered")
        sessionTaskProviderTableDao.getAll()
    }
    suspend fun getSessionTaskProviderByPrimaryKeys(
        indexDay: Int,
        taskId: Long,
        sessionId: Long,
    ) = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "getSessionTaskProviderByPrimaryKeys triggered with indexDay $indexDay, taskId $taskId, sessionId $sessionId")
        sessionTaskProviderTableDao.getByPrimaryKeys(indexDay, taskId, sessionId)
    }

    suspend fun insertSessionTaskProviderTable(sessionTaskProviderTable: SessionTaskProviderTable) = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "insertSessionTaskProviderTable: $sessionTaskProviderTable")
        sessionTaskProviderTableDao.insert(sessionTaskProviderTable)
    }

    suspend fun deleteSessionTaskProviderTableByPrimaryKeys(
        indexDay: Int,
        taskId: Long,
        sessionId: Long,
    ) = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "deleteSessionTaskProviderTableByPrimaryKeys: $indexDay, $taskId, $sessionId")
        sessionTaskProviderTableDao.deleteByIndexDayAndTaskIdAndSessionId(indexDay, taskId, sessionId)
    }

    suspend fun deleteSessionTaskProviderTable(sessionTaskProviderTable: SessionTaskProviderTable) = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "deleteSessionTaskProviderTable: $sessionTaskProviderTable")
        sessionTaskProviderTableDao.delete(sessionTaskProviderTable)
    }

    suspend fun updateSessionTaskProviderTable(sessionTaskProviderTable: SessionTaskProviderTable) = withContext(Dispatchers.IO) {
        sessionTaskProviderTableDao.update(sessionTaskProviderTable)
    }

    suspend fun updateSessionTaskProviderTableLocationByPrimaryKeys(
        indexDay: Int,
        taskId: Long,
        sessionId: Long,
        location: String,
        locationLink : String
    ) = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "updateSessionTaskProviderTableLocationByPrimaryKeys: $indexDay, $taskId, $sessionId, $location, $locationLink")
        sessionTaskProviderTableDao.updateLocationByPrimaryKeys(
            indexDay = indexDay,
            taskId = taskId,
            sessionId = sessionId,
            location = location,
            locationLink = locationLink
        )
    }

    suspend fun getTaskAndSessionJoinByProviderPrimaryKeys(
        indexDay: Int,
        taskId: Long,
        sessionId: Long,
    ) = withContext(Dispatchers.IO) {
        Log.d("AppDatabaseRepository", "getTaskAndSessionJoinByProviderPrimaryKeys: $indexDay, $taskId, $sessionId")
        sessionTaskProviderTableDao.getTaskAndSessionJoinByProviderPrimaryKeys(
            indexDay = indexDay,
            taskId = taskId,
            sessionId = sessionId
        )
    }

}
