package com.minizuure.todoplannereducationedition.services.database.temp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import com.minizuure.todoplannereducationedition.dialog_modal.model.DaysOfWeekImpl
import com.minizuure.todoplannereducationedition.services.database.session.SessionTable
import kotlinx.coroutines.flow.firstOrNull

class RescheduleFormViewModel : ViewModel() {
    val day : MutableLiveData<DaysOfWeekImpl?> = MutableLiveData(null)
    val session : MutableLiveData<SessionTable?> = MutableLiveData(null)
    val isCustomSession : MutableLiveData<Boolean> = MutableLiveData(false)
    val timeStart : MutableLiveData<String?> = MutableLiveData(null)
    val timeEnd : MutableLiveData<String?> = MutableLiveData(null)
    var isObserverActive : Boolean = false


    fun setDay(day :DaysOfWeekImpl) {
        this.day.value = day
    }

    suspend fun getDay() : DaysOfWeekImpl? {
        Log.d("TaskFormViewModel", "getDay: program reached")
        return day.asFlow().firstOrNull()
    }

    fun setSession(sessionTable: SessionTable?) {
        this.session.value = sessionTable
    }

    suspend fun getSession() : SessionTable? {
        return session.asFlow().firstOrNull()
    }

    fun setIsCustomSession(isCustomSession : Boolean) {
        this.isCustomSession.value = isCustomSession
    }

    suspend fun getIsCustomSession() : Boolean {
        return isCustomSession.asFlow().firstOrNull() ?: false
    }

    fun setTimeStart(text: String) {
        this.timeStart.value = text
    }

    suspend fun getTimeStart() : String? {
        return timeStart.asFlow().firstOrNull()
    }

    fun setTimeEnd(text: String) {
        this.timeEnd.value = text
    }

    suspend fun getTimeEnd() : String? {
        return timeEnd.asFlow().firstOrNull()
    }

    fun reset() {
        day.value = null
        session.value = null
        isCustomSession.value = false
        timeStart.value = null
        timeEnd.value = null
        isObserverActive = false
    }
}