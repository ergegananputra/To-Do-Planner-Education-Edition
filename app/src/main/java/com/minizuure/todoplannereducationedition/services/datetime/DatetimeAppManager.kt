package com.minizuure.todoplannereducationedition.services.datetime

import android.content.Context
import android.text.format.DateFormat
import androidx.fragment.app.FragmentManager
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Datetime Package
 *
 * Based on this article :
 * @see "https://phrase.com/blog/posts/localized-date-time-android/"
 *
 */
class DatetimeAppManager {

    private val zoneLocalTimeId: ZoneId = ZoneId.systemDefault()

    fun getLocalDateTime() : ZonedDateTime {
        return ZonedDateTime.now(zoneLocalTimeId)
    }

    private fun localizedUTC(dateTimeInUTCiso8601: String) : ZonedDateTime {
        val timestampInstant = Instant.parse(dateTimeInUTCiso8601)
        return ZonedDateTime.ofInstant(timestampInstant, zoneLocalTimeId)
    }


    private fun timePickerBuilder(context: Context) : MaterialTimePicker {
        val currentTime = DatetimeAppManager().getLocalDateTime()
        val clockFormat = if (DateFormat.is24HourFormat(context)) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        return MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(currentTime.hour)
            .setMinute(currentTime.minute)
            .setTitleText("Select start time")
            .build()
    }

    /**
     * Set EditText TimePickerDialog.
     * Use this function to set EditText TimePickerDialog and open Time Picker Dialog.
     *
     * example in SessionFormFragment.kt:
     * ```kotlin
     * DatetimeAppManager().setEditTextTimePickerDialog(
     *             requireContext(),
     *             parentFragmentManager,
     *             binding.textInputLayoutEndTime
     *         )
     * ```
     *
     * @param context : Context
     * @param parentFragmentManager : FragmentManager
     * @param textInputLayoutTime : TextInputLayout
     *
     * @see "https://github.com/material-components/material-components-android/blob/master/docs/components/TimePicker.md"
     */
    fun setEditTextTimePickerDialog(
        context: Context,
        parentFragmentManager: FragmentManager,
        textInputLayoutTime: TextInputLayout
    ) {
        val picker = timePickerBuilder(context)

        textInputLayoutTime.setEndIconOnClickListener {
            picker.show(parentFragmentManager, "SessionFormFragment")
        }

        picker.addOnPositiveButtonClickListener {
            val hour = picker.hour
            val minute = picker.minute
            val time = String.format("%02d:%02d", hour, minute)
            textInputLayoutTime.editText?.setText(time)
        }
    }


}