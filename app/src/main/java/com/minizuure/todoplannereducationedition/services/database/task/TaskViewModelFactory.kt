package com.minizuure.todoplannereducationedition.services.database.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.minizuure.todoplannereducationedition.AppDatabaseRepository

class TaskViewModelFactory(
    private val appDatabaseRepository: AppDatabaseRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(appDatabaseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}