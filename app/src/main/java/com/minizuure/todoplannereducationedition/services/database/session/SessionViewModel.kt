package com.minizuure.todoplannereducationedition.services.database.session

import android.util.Log
import androidx.lifecycle.ViewModel
import com.minizuure.todoplannereducationedition.AppDatabaseRepository

class SessionViewModel(
    private val appDatabaseRepository: AppDatabaseRepository
) : ViewModel() {

    suspend fun getAll() : List<SessionTable> {
        Log.d("SessionViewModel", "get all sessions")
        return appDatabaseRepository.getAllSessions()
    }

    suspend fun getById(id: Int) : SessionTable? {
        Log.d("SessionViewModel", "begin get session by id: $id")
        val result = appDatabaseRepository.getSessionById(id)
        Log.d("SessionViewModel", "result get session by id is ${result != null}")
        return result
    }

    suspend fun getPaginated(limit: Int, offset: Int = 0, search : String = "") : List<SessionTable> {
        Log.d("SessionViewModel", "get paginated sessions")
        return appDatabaseRepository.getPaginatedSessions(limit, offset, search)
    }

    suspend fun getByRoutineId(routineId: Int) : List<SessionTable> {
        Log.d("SessionViewModel", "get sessions by routine id: $routineId")
        return appDatabaseRepository.getByRoutineId(routineId)
    }

    suspend fun insert(sessionTable: SessionTable) {
        Log.d("SessionViewModel", "insert session: $sessionTable")
        appDatabaseRepository.insertSession(sessionTable)
    }

    suspend fun insert(
        title : String,
        timeStart : String,
        timeEnd : String,
        selectedDays : String,
        fkRoutineId : Int
    ) {
        Log.d("SessionViewModel", "insert session with params: $title, $timeStart, $timeEnd, $selectedDays, $fkRoutineId")
        val sessionTable = SessionTable(
            id = 0,
            title = title,
            timeStart = timeStart,
            timeEnd = timeEnd,
            selectedDays = selectedDays,
            fkRoutineId = fkRoutineId
        )
        appDatabaseRepository.insertSession(sessionTable)
    }

    suspend fun delete(sessionTable: SessionTable) {
        Log.d("SessionViewModel", "delete session: $sessionTable")
        appDatabaseRepository.deleteSession(sessionTable)
    }

    suspend fun update(sessionTable: SessionTable) {
        Log.d("SessionViewModel", "update session: $sessionTable")
        appDatabaseRepository.updateSession(sessionTable)
    }

    suspend fun search(searchQuery: String) : List<SessionTable> {
        Log.d("SessionViewModel", "search sessions with query: $searchQuery")
        return appDatabaseRepository.searchSessions(searchQuery)
    }

    suspend fun countSessionsForRoutine(routineId: Int) : Int {
        Log.d("SessionViewModel", "count sessions for routine: $routineId")
        return appDatabaseRepository.countSessionsForRoutine(routineId)
    }

    suspend fun getSessionsForRoutine(routineId: Int) : List<SessionTable> {
        Log.d("SessionViewModel", "get sessions for routine: $routineId")
        return appDatabaseRepository.getSessionsForRoutine(routineId)
    }

}