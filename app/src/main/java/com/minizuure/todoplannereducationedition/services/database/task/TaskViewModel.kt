package com.minizuure.todoplannereducationedition.services.database.task

import android.util.Log
import androidx.lifecycle.ViewModel
import com.minizuure.todoplannereducationedition.AppDatabaseRepository

class TaskViewModel(
    private val appDatabaseRepository: AppDatabaseRepository
) : ViewModel() {

    suspend fun getAll() : List<TaskTable> {
        Log.d("TaskViewModel", "get all tasks")
        return appDatabaseRepository.getAllTasks()
    }

    suspend fun getById(id: Int) : TaskTable? {
        Log.d("TaskViewModel", "begin get task by id: $id")
        val result = appDatabaseRepository.getTaskById(id)
        Log.d("TaskViewModel", "result get task by id is ${result != null}")
        return result
    }

    suspend fun getPaginated(limit: Int, offset: Int = 0) : List<TaskTable> {
        Log.d("TaskViewModel", "get paginated tasks")
        return appDatabaseRepository.getPaginatedTasks(limit, offset)
    }

    suspend fun getByIndexDay(indexDay: Int) : List<TaskTable> {
        Log.d("TaskViewModel", "get tasks by index day: $indexDay")
        return appDatabaseRepository.getTasksByIndexDay(indexDay)
    }

    suspend fun getBySessionId(sessionId: Int) : List<TaskTable> {
        Log.d("TaskViewModel", "get tasks by session id: $sessionId")
        return appDatabaseRepository.getTasksBySessionId(sessionId)
    }

    suspend fun insert(taskTable: TaskTable) {
        Log.d("TaskViewModel", "insert task: $taskTable")
        appDatabaseRepository.insertTask(taskTable)
    }

    suspend fun insert(
        title : String,
        indexDay : Int,
        sessionId : Int,
        isCustomSession : Boolean = false,
        startTime : String? = null,
        endTime : String? = null,
        locationName : String? = null,
        locationAddress : String? = null,
        isSharedToCommunity : Boolean = false,
        communityId : String? = null
    ) {
        Log.d("TaskViewModel", "insert task with params: $title, $indexDay, $sessionId")
        var timeStart : String = startTime ?: "00:00"
        var timeEnd : String = endTime ?: "00:00"

        if (isCustomSession) {
            val sessionTable = appDatabaseRepository.getSessionById(sessionId)
            Log.d("TaskViewModel", "sessionTable is not null: ${sessionTable != null} ${sessionTable?.time_start} ${sessionTable?.time_end}")

            if (sessionTable != null) {
                timeStart = sessionTable.time_start
                timeEnd = sessionTable.time_end
            }

        }

        appDatabaseRepository.insertTask(
            TaskTable(
                title = title,
                indexDay = indexDay,
                sessionId = sessionId,
                isCustomSession = isCustomSession,
                startTime = timeStart,
                endTime = timeEnd,
                locationName = locationName,
                locationAddress = locationAddress,
                isSharedToCommunity = isSharedToCommunity,
                communityId = communityId
            )
        )
    }


    suspend fun delete(taskTable: TaskTable) {
        Log.d("TaskViewModel", "delete task: $taskTable")
        appDatabaseRepository.deleteTask(taskTable)
    }

    suspend fun update(taskTable: TaskTable) {
        Log.d("TaskViewModel", "update task: $taskTable")
        appDatabaseRepository.updateTask(taskTable)
    }

}