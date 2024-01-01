package com.minizuure.todoplannereducationedition.services.database.routine

import androidx.lifecycle.ViewModel
import com.minizuure.todoplannereducationedition.AppDatabaseRepository

class RoutineViewModel(
    private val appDatabaseRepository: AppDatabaseRepository
) : ViewModel() {

    suspend fun getAll() : List<RoutineTable> {
        return appDatabaseRepository.getAllRoutines()
    }

    suspend fun getById(id: Int) : RoutineTable? {
        return appDatabaseRepository.getRoutineById(id)
    }

    suspend fun getPaginated(limit: Int, offset: Int = 0, search : String = "") : List<RoutineTable> {
        return appDatabaseRepository.getPaginatedRoutines(limit, offset, search)
    }

    suspend fun insert(routineTable: RoutineTable) {
        appDatabaseRepository.insertRoutine(routineTable)
    }

    suspend fun insert(
        title : String,
        description : String,
        dateStart : String,
        dateEnd : String,
    ) {
        val routineTable = RoutineTable(
            id = 0,
            title = title,
            description = description,
            date_start = dateStart,
            date_end = dateEnd
        )
        appDatabaseRepository.insertRoutine(routineTable)
    }

    suspend fun delete(routineTable: RoutineTable) {
        appDatabaseRepository.deleteRoutine(routineTable)
    }

    suspend fun update(routineTable: RoutineTable) {
        appDatabaseRepository.updateRoutine(routineTable)
    }
}