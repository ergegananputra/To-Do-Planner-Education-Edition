package com.minizuure.todoplannereducationedition.services.database.relations_table

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minizuure.todoplannereducationedition.AppDatabaseRepository
import com.minizuure.todoplannereducationedition.services.database.join.TaskAndSessionJoin
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    /**
     *
     * [optimizeSessionTaskProviderTable]
     *
     *
     * Delete all [SessionTaskProviderTable] that have a [SessionTaskProviderTable.rescheduledDateStart] that is
     * after the [SessionTaskProviderTable.rescheduledDateEnd].
     *
     *
     * [SessionTaskProviderTable.rescheduledDateStart] and [SessionTaskProviderTable.rescheduledDateEnd] must be in ISO format
     * and always accurate to the day plus the zone hours.
     * Example: "2024-01-28T17:00:00Z"
     */
    fun optimizeSessionTaskProviderTable() {
       viewModelScope.launch(Dispatchers.IO) {
           val allProviders = getAll()

           for (item in allProviders) {
               if (!item.isRescheduled) continue // if not rescheduled, continue

               val dateStart = DatetimeAppManager(item.rescheduledDateStart!!).selectedDetailDatetimeISO
               val dateEnd = DatetimeAppManager(item.rescheduledDateEnd!!).selectedDetailDatetimeISO

               if (dateStart.isBefore(dateEnd) || dateStart.isEqual(dateEnd)) continue

               delete(item)
           }
       }
    }

}