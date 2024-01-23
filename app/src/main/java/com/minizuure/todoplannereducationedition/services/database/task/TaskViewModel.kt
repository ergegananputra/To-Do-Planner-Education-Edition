package com.minizuure.todoplannereducationedition.services.database.task

import android.util.Log
import androidx.lifecycle.ViewModel
import com.minizuure.todoplannereducationedition.AppDatabaseRepository
import com.minizuure.todoplannereducationedition.services.database.join.TaskAndSessionJoin
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager
import java.time.ZonedDateTime

class TaskViewModel(
    private val appDatabaseRepository: AppDatabaseRepository
) : ViewModel() {

    suspend fun getAll() : List<TaskTable> {
        Log.d("TaskViewModel", "get all tasks")
        return appDatabaseRepository.getAllTasks()
    }

    suspend fun getById(id: Long) : TaskTable? {
        Log.d("TaskViewModel", "begin get task by id: $id")
        val result = appDatabaseRepository.getTaskById(id)
        Log.d("TaskViewModel", "result get task by id is ${result != null}")
        return result
    }

    suspend fun getPaginated(limit: Int, offset: Int = 0) : List<TaskTable> {
        Log.d("TaskViewModel", "get paginated tasks")
        return appDatabaseRepository.getPaginatedTasks(limit, offset)
    }

    suspend fun getByIndexDay(indexDay: Int, selectedDateTime: ZonedDateTime) : List<TaskTable> {
        Log.d("TaskViewModel", "get tasks by index day: $indexDay")
        return appDatabaseRepository.getTasksByIndexDay(indexDay, selectedDateTime)
    }

    suspend fun getJoinSessionByIndexDay(
        indexDay: Int,
        selectedDateTime: ZonedDateTime,
        isToday: Boolean,
    ) : List<TaskAndSessionJoin> {
        Log.d("TaskViewModel", "get tasks by index day: $indexDay")
        val datetimeAppManager = DatetimeAppManager().selectedDetailDatetimeISO
        val hour = datetimeAppManager.hour.let {
            if (it < 10) "0$it" else "$it"
        }
        val minute = datetimeAppManager.minute.let {
            if (it < 10) "0$it" else "$it"
        }

        return appDatabaseRepository.getTaskAndSessionJoinByIndexDay(
            indexDay = indexDay,
            selectedDate = selectedDateTime.toLocalDateTime(),
            isToday = isToday,
            todayHour = "$hour:$minute",
        )
    }

    suspend fun getBySessionId(sessionId: Long) : List<TaskTable> {
        Log.d("TaskViewModel", "get tasks by session id: $sessionId")
        return appDatabaseRepository.getTasksBySessionId(sessionId)
    }

    suspend fun insert(taskTable: TaskTable) : Long {
        Log.d("TaskViewModel", "insert task: $taskTable")
        return appDatabaseRepository.insertTask(taskTable)
    }

    suspend fun insert(
        title : String,
        indexDay : Int,
        sessionId : Long,
        locationName : String? = null,
        locationAddress : String? = null,
        isSharedToCommunity : Boolean = false,
        communityId : String? = null
    ) : Long {
        return appDatabaseRepository.insertTask(
            TaskTable(
                title = title,
                indexDay = indexDay,
                sessionId = sessionId,
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

    suspend fun deleteById(id: Long) {
        Log.d("TaskViewModel", "delete task by id: $id")
        val taskTable = appDatabaseRepository.getTaskById(id) ?: return
        appDatabaseRepository.deleteTask(taskTable)
    }

    suspend fun update(taskTable: TaskTable) {
        Log.d("TaskViewModel", "update task: $taskTable")
        appDatabaseRepository.updateTask(taskTable)
    }

}