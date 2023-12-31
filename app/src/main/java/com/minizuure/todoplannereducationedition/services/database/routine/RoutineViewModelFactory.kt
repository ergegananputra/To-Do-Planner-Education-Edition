package com.minizuure.todoplannereducationedition.services.database.routine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.minizuure.todoplannereducationedition.AppDatabaseRepository

class RoutineViewModelFactory(
    private val appDatabaseRepository: AppDatabaseRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoutineViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RoutineViewModel(appDatabaseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}