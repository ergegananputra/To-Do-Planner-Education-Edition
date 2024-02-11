package com.minizuure.todoplannereducationedition.services.database.reservasion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.minizuure.todoplannereducationedition.AppDatabaseRepository

class ReservationViewModelFactory(
    private val appDatabaseRepository: AppDatabaseRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReservationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReservationViewModel(appDatabaseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}