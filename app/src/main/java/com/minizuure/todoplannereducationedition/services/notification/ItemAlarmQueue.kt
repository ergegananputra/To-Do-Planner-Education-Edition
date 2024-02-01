package com.minizuure.todoplannereducationedition.services.notification

import java.time.ZonedDateTime

/**
 * This class is used to store the data of the alarm queue.
 *
 *
 * Please note that the id is use this convention:
 *
 *
 * X_DDM_MY0_000 (max digit of integer)
 * - Don't used digit X,
 *
 * - DD is the day of the alarm queue.
 *
 * - MM is the month of the alarm queue.
 *
 * - Y is the year of the alarm queue. for example 2021 is 1, 2022 is 2, 2023 is 3, etc.
 *
 * - The last 0000 is for the id of the task
 *
 *
 * for example:
 *
 *
 * 22 Desember 2024 with task id 19
 *
 *
 * the id should be 221240019
 *
 *
 * [convertIdToDaysBeforeID] digunakan untuk mengubah id menjadi id khusus untuk
 * notifikasi yang akan dikirimkan beberapa hari sebelumnya.
 *
 *
 * @param id The id of the alarm queue. Note : 9 Chars Only.
 * @param action The action of the alarm queue.
 * @param taskId The task id of the alarm queue.
 * @param time The time of the alarm queue.
 * @param taskName The task name of the alarm queue.
 * @param message The message of the alarm queue.
 * @param taskDateIdentification The task date identification of the alarm queue.
 */
data class ItemAlarmQueue(
    val id : Int = 0,
    val action : String = "",
    val taskId : Long = 0,
    val time : ZonedDateTime = ZonedDateTime.now(),
    val taskName : String = "",
    val message : String = "",
    val taskDateIdentification : ZonedDateTime = ZonedDateTime.now(),
) {
    //                              X_DDM_MY0_000
    private val dayBeforeIDsConstanta = 4_000_000
    fun convertIdToDaysBeforeID() : Int {
        return id + dayBeforeIDsConstanta
    }

    fun isDaysBeforeID() : Boolean {
        val month = id.toString().substring(2, 4).toInt()
        return month > 40
    }

    fun isNotDaysBeforeID() : Boolean {
        return !isDaysBeforeID()
    }

    fun createItemId(days : Int, months : Int, years : Int, taskId : Long) : Int {
        val day = if (days < 10) {
            "0$days"
        } else {
            days.toString()
        }

        val month = if (months < 10) {
            "0$months"
        } else {
            months.toString()
        }

        val year = years % 10

        return "$day$month$year${taskId.toString().padStart(4, '0')}".toInt()
    }
}

