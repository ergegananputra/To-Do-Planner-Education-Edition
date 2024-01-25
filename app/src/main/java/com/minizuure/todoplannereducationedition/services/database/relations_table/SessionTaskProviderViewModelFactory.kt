package com.minizuure.todoplannereducationedition.services.database.relations_table

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.minizuure.todoplannereducationedition.AppDatabaseRepository

class SessionTaskProviderViewModelFactory(
    private val appDatabaseRepository: AppDatabaseRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SessionTaskProviderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SessionTaskProviderViewModel(appDatabaseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}