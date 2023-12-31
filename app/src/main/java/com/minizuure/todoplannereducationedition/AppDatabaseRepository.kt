package com.minizuure.todoplannereducationedition

import com.minizuure.todoplannereducationedition.services.database.ApplicationDatabase
import com.minizuure.todoplannereducationedition.services.database.DeleteAllOperation
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineTable
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineTableDao
import com.minizuure.todoplannereducationedition.services.database.session.SessionTable
import com.minizuure.todoplannereducationedition.services.database.session.SessionTableDao
import com.minizuure.todoplannereducationedition.services.database.task.TaskTable
import com.minizuure.todoplannereducationedition.services.database.task.TaskTableDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
    private val deleteAllOperation: DeleteAllOperation
) {
    // Detele
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
    suspend fun getRoutineById(id: Int) = withContext(Dispatchers.IO) { routineTableDao.getById(id) }
    suspend fun getPaginatedRoutines(limit: Int, offset: Int = 0, searchQuery: String = "") =
        withContext(Dispatchers.IO) {
            routineTableDao.getPaginated(limit, offset, "%$searchQuery%")
        }
    suspend fun insertRoutine(routineTable: RoutineTable) = withContext(Dispatchers.IO) { routineTableDao.insert(routineTable) }
    suspend fun deleteRoutine(routineTable: RoutineTable) = withContext(Dispatchers.IO) { routineTableDao.delete(routineTable) }
    suspend fun updateRoutine(routineTable: RoutineTable) = withContext(Dispatchers.IO) { routineTableDao.update(routineTable) }


    // SessionTableDao
    suspend fun getAllSessions() = withContext(Dispatchers.IO) { sessionTableDao.getAll() }
    suspend fun getSessionById(id: Int) = withContext(Dispatchers.IO) { sessionTableDao.getById(id) }
    suspend fun getPaginatedSessions(limit: Int, offset: Int = 0, searchQuery: String = "") =
        withContext(Dispatchers.IO) {
            sessionTableDao.getPaginated(limit, offset, "%$searchQuery%")
        }
    suspend fun getByRoutineId(routineId: Int) = withContext(Dispatchers.IO) { sessionTableDao.getByRoutineId(routineId) }
    suspend fun insertSession(sessionTable: SessionTable) = withContext(Dispatchers.IO) { sessionTableDao.insert(sessionTable) }
    suspend fun deleteSession(sessionTable: SessionTable) = withContext(Dispatchers.IO) { sessionTableDao.delete(sessionTable) }
    suspend fun updateSession(sessionTable: SessionTable) = withContext(Dispatchers.IO) { sessionTableDao.update(sessionTable) }
    suspend fun searchSessions(searchQuery: String) = withContext(Dispatchers.IO) { sessionTableDao.search("%$searchQuery%") }
    suspend fun countSessionsForRoutine(routineId: Int) = withContext(Dispatchers.IO) { sessionTableDao.countSessionsForRoutine(routineId) }
    suspend fun getSessionsForRoutine(routineId: Int) = withContext(Dispatchers.IO) { sessionTableDao.getSessionsForRoutine(routineId) }


    // TaskTableDao
    suspend fun getAllTasks() = withContext(Dispatchers.IO) { taskTableDao.getAll() }
    suspend fun getTaskById(id: Int) = withContext(Dispatchers.IO) { taskTableDao.getById(id) }
    suspend fun getPaginatedTasks(limit: Int, offset: Int = 0) =
        withContext(Dispatchers.IO) {
            taskTableDao.getPaginated(limit, offset)
        }
    suspend fun getTasksByIndexDay(indexDay: Int) = withContext(Dispatchers.IO) { taskTableDao.getByIndexDay(indexDay) }
    suspend fun getTasksBySessionId(sessionId: Int) = withContext(Dispatchers.IO) { taskTableDao.getBySessionId(sessionId) }
    suspend fun insertTask(taskTable: TaskTable) = withContext(Dispatchers.IO) { taskTableDao.insert(taskTable) }
    suspend fun deleteTask(taskTable: TaskTable) = withContext(Dispatchers.IO) { taskTableDao.delete(taskTable) }
    suspend fun updateTask(taskTable: TaskTable) = withContext(Dispatchers.IO) { taskTableDao.update(taskTable) }

}
