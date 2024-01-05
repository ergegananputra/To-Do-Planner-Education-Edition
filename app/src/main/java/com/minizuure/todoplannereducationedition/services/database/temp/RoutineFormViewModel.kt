package com.minizuure.todoplannereducationedition.services.database.temp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import com.minizuure.todoplannereducationedition.model.TempSession
import kotlinx.coroutines.flow.first

class RoutineFormViewModel : ViewModel() {
    val tempSessions : MutableLiveData<MutableList<TempSession>> = MutableLiveData(mutableListOf())
    private suspend fun size() : Int {
        return tempSessions.asFlow().first().size
    }

    suspend fun addTempSession(
        title : String,
        startTime : String,
        endTime : String,
        daysSelected : String
    ) {
        Log.d("RoutineFormViewModel", "addTempSession: inserting")
        tempSessions.asFlow().first().add(
            TempSession(
                index = size(),
                title = title,
                startTime = startTime,
                endTime = endTime,
                daysSelected = daysSelected
            )
        )
    }

    suspend fun updateTempSession(
        index : Int,
        title : String,
        startTime : String,
        endTime : String,
        daysSelected : String
    ) {
        Log.d("RoutineFormViewModel", "updateTempSession: updating $index")
        tempSessions.asFlow().first()[index] = TempSession(
            index = index,
            title = title,
            startTime = startTime,
            endTime = endTime,
            daysSelected = daysSelected
        )
    }

    suspend fun deleteTempSession(index : Int) {
        Log.d("RoutineFormViewModel", "deleteTempSession: deleting $index")
        tempSessions.asFlow().first().removeAt(index)
    }


    suspend fun clearTempSessions() {
        Log.d("RoutineFormViewModel", "clearTempSessions: clearing")
        tempSessions.asFlow().first().clear()
    }



}