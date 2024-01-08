package com.minizuure.todoplannereducationedition.services.database.temp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineTable
import com.minizuure.todoplannereducationedition.services.database.session.SessionTable

class TaskFormViewModel : ViewModel() {
    val routineTemplate : MutableLiveData<RoutineTable> = MutableLiveData()
    val day : MutableLiveData<Pair<Long, String>> = MutableLiveData()
    val session : MutableLiveData<SessionTable> = MutableLiveData()
    val timeStart : MutableLiveData<Pair<Long, String>> = MutableLiveData()
    val timeEnd : MutableLiveData<Pair<Long, String>> = MutableLiveData()

    fun setRoutineTemplate(routineTemplate : RoutineTable) {
        this.routineTemplate.value = routineTemplate
    }

    fun setDay(id : Long, text: String) {
        this.day.value = Pair(id, text)
    }

    fun setSession(sessionTable: SessionTable) {
        this.session.value = sessionTable
    }

    fun setTimeStart(id : Long, text: String) {
        this.timeStart.value = Pair(id, text)
    }

    fun setTimeEnd(id : Long, text: String) {
        this.timeEnd.value = Pair(id, text)
    }
}