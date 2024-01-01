package com.minizuure.todoplannereducationedition.services.database.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.minizuure.todoplannereducationedition.AppDatabaseRepository

class SessionViewModelFactory(
    private val appDatabaseRepository: AppDatabaseRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SessionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SessionViewModel(appDatabaseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}