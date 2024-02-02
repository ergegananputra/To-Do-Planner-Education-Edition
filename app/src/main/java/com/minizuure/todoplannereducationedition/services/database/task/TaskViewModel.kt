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
            selectedDate = selectedDateTime,
            isToday = isToday,
            todayHour = "$hour:$minute",
        )
    }

    suspend fun getJoinSessionByTaskId(
        taskId: Long,
        indexDay: Int,
        selectedDateTime: ZonedDateTime,
        ) : TaskAndSessionJoin? {
        Log.d("TaskViewModel", "get tasks by task id: $taskId")
        return appDatabaseRepository.getTaskAndSessionJoinByTaskId(
            taskId = taskId,
            indexDay = indexDay,
            selectedDate = selectedDateTime,
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
        isSharedToCommunity : Boolean = false,
        communityId : String? = null
    ) : Long {
        return appDatabaseRepository.insertTask(
            TaskTable(
                title = title,
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

    suspend fun getTaskAndSessionJoinByProviderPrimaryKeys(
        indexDay: Int,
        taskId: Long,
        sessionId: Long,
        paramDateIso8601 : String
    ) : TaskAndSessionJoin? {
        return appDatabaseRepository.getTaskAndSessionJoinByProviderPrimaryKeys(
            indexDay = indexDay,
            taskId = taskId,
            sessionId = sessionId,
            paramDateIso8601 = paramDateIso8601
        )
    }

    /**
     *
     *  available [searchModel] :
     *
     *
     *  1. "byNotesDates" : Search with references to the notes dates, this may not include all the
     *  tasks that are not related to the notes dates
     *  2. "byProvider" : Search with references to the provider table, this not efficient for the
     *  search but almost include all the tasks
     */
    suspend fun search(
        query: String,
        selectedDate : ZonedDateTime,
        searchModel : Int
    ) : List<TaskAndSessionJoin> {
        val searchModelDict = mapOf(
            1 to "byNotesDates",
            2 to "byProviders"
        )
        val dateISO8601 = DatetimeAppManager(selectedDate, true).dateISO8601inString
        return appDatabaseRepository.searchTasks(query, dateISO8601, searchModelDict[searchModel] ?: searchModelDict[1]!!)
    }

}