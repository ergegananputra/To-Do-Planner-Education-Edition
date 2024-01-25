package com.minizuure.todoplannereducationedition.services.database.relations_table

import androidx.lifecycle.ViewModel
import com.minizuure.todoplannereducationedition.AppDatabaseRepository
import com.minizuure.todoplannereducationedition.services.database.join.TaskAndSessionJoin

class SessionTaskProviderViewModel(
    private val appDatabaseRepository: AppDatabaseRepository
) : ViewModel() {

    suspend fun getAll() : List<SessionTaskProviderTable> {
        return appDatabaseRepository.getAllSessionTaskProviderTable()
    }

    suspend fun getByPrimaryKeys(
        indexDay: Int,
        taskId: Long,
        sessionId: Long
    ) : SessionTaskProviderTable? {
        return appDatabaseRepository.getSessionTaskProviderByPrimaryKeys(
            indexDay = indexDay,
            taskId = taskId,
            sessionId = sessionId
        )
    }

    suspend fun insert(sessionTaskProviderTable: SessionTaskProviderTable) : Long {
        return appDatabaseRepository.insertSessionTaskProviderTable(sessionTaskProviderTable)
    }

    suspend fun deleteByIndexDayAndTaskIdAndSessionId(
        indexDay: Int,
        taskId: Long,
        sessionId: Long
    ) {
        appDatabaseRepository.deleteSessionTaskProviderTableByPrimaryKeys(
            indexDay = indexDay,
            taskId = taskId,
            sessionId = sessionId
        )
    }

    suspend fun delete(sessionTaskProviderTable: SessionTaskProviderTable) {
        appDatabaseRepository.deleteSessionTaskProviderTable(sessionTaskProviderTable)
    }

    suspend fun update(sessionTaskProviderTable: SessionTaskProviderTable) {
        appDatabaseRepository.updateSessionTaskProviderTable(sessionTaskProviderTable)
    }

    suspend fun updateLocationByPrimaryKeys(
        indexDay: Int,
        taskId: Long,
        sessionId: Long,
        location: String,
        locationLink : String
    ) {
        appDatabaseRepository.updateSessionTaskProviderTableLocationByPrimaryKeys(
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
        sessionId: Long
    ) : TaskAndSessionJoin? {
        return appDatabaseRepository.getTaskAndSessionJoinByProviderPrimaryKeys(
            indexDay = indexDay,
            taskId = taskId,
            sessionId = sessionId
        )
    }

}