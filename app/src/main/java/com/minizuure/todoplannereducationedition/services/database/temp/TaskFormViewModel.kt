package com.minizuure.todoplannereducationedition.services.database.temp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import com.minizuure.todoplannereducationedition.dialog_modal.model.DaysOfWeekImpl
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineTable
import com.minizuure.todoplannereducationedition.services.database.session.SessionTable
import kotlinx.coroutines.flow.first

class TaskFormViewModel : ViewModel() {
    val routineTemplate : MutableLiveData<RoutineTable> = MutableLiveData()
    val day : MutableLiveData<DaysOfWeekImpl> = MutableLiveData()
    val session : MutableLiveData<SessionTable> = MutableLiveData()
    val isCustomSession : MutableLiveData<Boolean> = MutableLiveData()
    val timeStart : MutableLiveData<String> = MutableLiveData()
    val timeEnd : MutableLiveData<String> = MutableLiveData()
    var isObserverActive : Boolean = false

    fun setRoutineTemplate(routineTemplate : RoutineTable) {
        this.routineTemplate.value = routineTemplate
    }

    suspend fun getRoutineTemplate() : RoutineTable? {
        return routineTemplate.asFlow().first()
    }

    fun setDay(day :DaysOfWeekImpl) {
        this.day.value = day
    }

    suspend fun getDay() : DaysOfWeekImpl? {
        return day.asFlow().first()
    }

    fun setSession(sessionTable: SessionTable?) {
        this.session.value = sessionTable
    }

    suspend fun getSession() : SessionTable? {
        return session.asFlow().first()
    }

    fun setIsCustomSession(isCustomSession : Boolean) {
        this.isCustomSession.value = isCustomSession
    }

    suspend fun getIsCustomSession() : Boolean {
        return isCustomSession.asFlow().first() ?: false
    }

    fun setTimeStart(text: String) {
        this.timeStart.value = text
    }

    suspend fun getTimeStart() : String? {
        return timeStart.asFlow().first()
    }

    fun setTimeEnd(text: String) {
        this.timeEnd.value = text
    }

    suspend fun getTimeEnd() : String? {
        return timeEnd.asFlow().first()
    }
}