package com.minizuure.todoplannereducationedition.surface.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModel
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    var _routineSize : Int = 0
    val routineSize : Int
        get() = _routineSize

    fun refreshRoutineSize(routineViewModel: RoutineViewModel) {
        viewModelScope.launch {
            routineViewModel.getCount().let { _routineSize = it }
        }
    }
}