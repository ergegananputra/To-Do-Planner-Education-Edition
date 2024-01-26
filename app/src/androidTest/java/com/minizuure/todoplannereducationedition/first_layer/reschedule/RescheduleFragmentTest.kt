package com.minizuure.todoplannereducationedition.first_layer.reschedule

import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager



class RescheduleFragmentTest {
    private val alternateDateString = "2024-01-28T23:59:59Z"
    private val weekFromNow = 1
    private val taskAndSessionJoinIndexDay = 1
    private val isRescheduleAllFollowingMeet = true
    private val selectedDatetimeDetailIso = DatetimeAppManager("2024-02-02T13:59:59Z").selectedDetailDatetimeISO

    private fun getAlternateDate() = DatetimeAppManager(
        DatetimeAppManager(alternateDateString).selectedDetailDatetimeISO,
        accuracyOnlyUpToDays = true
    )


    /**
     * This [currentWeekDateAccuracyTest] test method is based on rescheduleTask() method in RescheduleFragment.kt
     */
    @org.junit.Test
    fun currentWeekDateAccuracyTest() {
        val alternateDate = getAlternateDate()

        val preCurrentWeekDate = DatetimeAppManager(selectedDatetimeDetailIso, accuracyOnlyUpToDays = true).selectedDetailDatetimeISO

        val currentWeekDate = preCurrentWeekDate.plusWeeks(weekFromNow.toLong()).minusDays(taskAndSessionJoinIndexDay.toLong() + 1).let {
            if (isRescheduleAllFollowingMeet) {
                if (alternateDate.selectedDetailDatetimeISO.isAfter(it)) it else it.minusWeeks(1)
            } else {
                it
            }
        }



        assert(currentWeekDate.hour == 0 || currentWeekDate.minute == 0) {
            "currentWeekDate should be at 00:00, but it is $currentWeekDate"
        }

    }

    /**
     * This [alternateDateAccuracyTest] test method is based on rescheduleTask() method in RescheduleFragment.kt
     */
    @org.junit.Test
    fun alternateDateAccuracyTest() {
        val alternateDate = getAlternateDate()

        // Add assertions here to verify the results of the date calculations
        assert(alternateDate.selectedDetailDatetimeISO.hour == 0 || alternateDate.selectedDetailDatetimeISO.minute == 0) {
            "alternateDate should be at 00:00, but it is ${alternateDate.selectedDetailDatetimeISO}"
        }
    }
}