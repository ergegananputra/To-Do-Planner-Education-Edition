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

    suspend fun getById(id: Long) : SessionTable? {
        Log.d("SessionViewModel", "begin get session by id: $id")
        val result = appDatabaseRepository.getSessionById(id)
        Log.v("SessionViewModel", "result get session with ID $id is ${if (result != null) "success " else "fail"}")
        return result
    }

    suspend fun getPaginated(limit: Int, offset: Int = 0, search : String = "") : List<SessionTable> {
        Log.d("SessionViewModel", "get paginated sessions")
        return appDatabaseRepository.getPaginatedSessions(limit, offset, search)
    }

    suspend fun getByRoutineId(routineId: Long) : List<SessionTable> {
        Log.d("SessionViewModel", "get sessions by routine id: $routineId")
        return appDatabaseRepository.getByRoutineId(routineId)
    }

    suspend fun insert(sessionTable: SessionTable) : Long {
        Log.d("SessionViewModel", "insert session: $sessionTable")
        return appDatabaseRepository.insertSession(sessionTable)
    }

    suspend fun insert(
        title : String,
        timeStart : String,
        timeEnd : String,
        selectedDays : String,
        fkRoutineId : Long,
        isCustomSession: Boolean = false
    ) : Long {
        Log.d("SessionViewModel", "insert session with params: $title, $timeStart, $timeEnd, $selectedDays, $fkRoutineId")
        val sessionTable = SessionTable(
            title = title,
            timeStart = timeStart,
            timeEnd = timeEnd,
            selectedDays = selectedDays,
            fkRoutineId = fkRoutineId,
            isCustomSession = isCustomSession
        )
        return appDatabaseRepository.insertSession(sessionTable)
    }

    suspend fun delete(sessionTable: SessionTable) {
        Log.d("SessionViewModel", "delete session: $sessionTable")
        appDatabaseRepository.deleteSession(sessionTable)
    }

    suspend fun update(sessionTable: SessionTable) {
        Log.d("SessionViewModel", "update session: $sessionTable")
        appDatabaseRepository.updateSession(sessionTable)
    }

    suspend fun updateCustomSession(
        sessionId: Long,
        isCustomSession: Boolean,
        timeStart: String = "",
        timeEnd: String = "",
    ) {
        Log.d("SessionViewModel", "update custom session status: $sessionId, $isCustomSession")
        val sessionTable = appDatabaseRepository.getSessionById(sessionId)
        if (sessionTable != null && isCustomSession) {
            sessionTable.isCustomSession = true
            sessionTable.timeStart = timeStart
            sessionTable.timeEnd = timeEnd
            appDatabaseRepository.updateSession(sessionTable)
        }
        else if (sessionTable != null) {
            sessionTable.isCustomSession = false
            appDatabaseRepository.updateSession(sessionTable)
        }
    }

    suspend fun search(searchQuery: String) : List<SessionTable> {
        Log.d("SessionViewModel", "search sessions with query: $searchQuery")
        return appDatabaseRepository.searchSessions(searchQuery)
    }

    suspend fun countSessionsForRoutine(routineId: Long) : Int {
        Log.d("SessionViewModel", "count sessions for routine: $routineId")
        return appDatabaseRepository.countSessionsForRoutine(routineId)
    }

    suspend fun getSessionsForRoutine(routineId: Long, isCustomSessionIncluded : Boolean) : List<SessionTable> {
        Log.d("SessionViewModel", "get sessions for routine: $routineId")
        return appDatabaseRepository.getSessionsForRoutine(routineId, isCustomSessionIncluded)
    }

}