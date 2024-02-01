package com.minizuure.todoplannereducationedition.services.database.routine

import androidx.lifecycle.ViewModel
import com.minizuure.todoplannereducationedition.AppDatabaseRepository

class RoutineViewModel(
    private val appDatabaseRepository: AppDatabaseRepository
) : ViewModel() {

    suspend fun getAll() : List<RoutineTable> {
        return appDatabaseRepository.getAllRoutines()
    }

    suspend fun getCount() : Int {
        return appDatabaseRepository.getRoutinesCount()
    }

    suspend fun getById(id: Long) : RoutineTable? {
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
    ) : Long {
        val routineTable = RoutineTable(
            title = title,
            description = description,
            date_start = dateStart,
            date_end = dateEnd
        )
        return appDatabaseRepository.insertRoutine(routineTable)
    }

    // delete by id

    suspend fun delete(routineTable: RoutineTable) {
        appDatabaseRepository.deleteRoutine(routineTable)
    }

    suspend fun update(routineTable: RoutineTable) {
        appDatabaseRepository.updateRoutine(routineTable)
    }

    suspend fun update(
        id : Long,
        title : String,
        description : String,
        dateStart : String,
        dateEnd : String,
    ) {
        val routineTable = RoutineTable(
            id = id,
            title = title,
            description = description,
            date_start = dateStart,
            date_end = dateEnd
        )
        appDatabaseRepository.updateRoutine(routineTable)
    }
}