package com.minizuure.todoplannereducationedition.services.database.temp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import com.minizuure.todoplannereducationedition.model.TempSession
import kotlinx.coroutines.flow.first

class RoutineFormViewModel : ViewModel() {
    val tempSessions : MutableLiveData<MutableList<TempSession>> = MutableLiveData(mutableListOf())
    private suspend fun automaticId() : Int {
        if (tempSessions.asFlow().first().isEmpty()) {
            return 0
        }
        return tempSessions.asFlow().first().last().id + 1
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
                id = automaticId(),
                title = title,
                startTime = startTime,
                endTime = endTime,
                daysSelected = daysSelected
            )
        )
    }

    suspend fun updateTempSession(
        id : Int,
        title : String,
        startTime : String,
        endTime : String,
        daysSelected : String
    ) {
        Log.d("RoutineFormViewModel", "updateTempSession: updating $id")
        tempSessions.asFlow().first()[id] = TempSession(
            id = id,
            title = title,
            startTime = startTime,
            endTime = endTime,
            daysSelected = daysSelected
        )
    }

    suspend fun deleteTempSession(id : Int) {
        Log.d("RoutineFormViewModel", "deleteTempSession: deleting $id")
        val index = tempSessions.asFlow().first().indexOfFirst { it.id == id }
        tempSessions.asFlow().first().removeAt(index)
    }


    suspend fun clearTempSessions() {
        Log.d("RoutineFormViewModel", "clearTempSessions: clearing")
        tempSessions.asFlow().first().clear()
    }



}