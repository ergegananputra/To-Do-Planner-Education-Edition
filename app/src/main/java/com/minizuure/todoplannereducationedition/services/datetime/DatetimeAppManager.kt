package com.minizuure.todoplannereducationedition.services.datetime

import android.content.Context
import android.text.format.DateFormat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.minizuure.todoplannereducationedition.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

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

    fun convertReadableDateToIso8601(readableDatetime: String) : String {
        val readableDate = LocalDate.parse(readableDatetime, DateTimeFormatter.ofPattern("dd LLLL yyyy"))
        val zonedDateTime = readableDate.atStartOfDay(zoneLocalTimeId)
        return zonedDateTime.toInstant().toString()
    }


    private fun timePickerBuilder(context: Context, title: String) : MaterialTimePicker {
        val currentTime = DatetimeAppManager().getLocalDateTime()
        val clockFormat = if (DateFormat.is24HourFormat(context)) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        return MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(currentTime.hour)
            .setMinute(currentTime.minute)
            .setTitleText(title)
            .build()
    }


    private fun datePickerBuilder(context: Context, title : String, forwardOnly: Boolean) : MaterialDatePicker<Long> {
        val datePickerBuilder = MaterialDatePicker.Builder.datePicker()
            .setTitleText(title)

        if (forwardOnly) {
            val constraintsBuilder = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())

            datePickerBuilder.setCalendarConstraints(constraintsBuilder.build())
        }

        return datePickerBuilder.build()
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
        textInputLayoutTime: TextInputLayout,
        title: String = "Select time"
    ) {
        val picker = timePickerBuilder(context, title)

        textInputLayoutTime.setEndIconOnClickListener {
            picker.show(parentFragmentManager, "SessionFormFragment")
        }

        picker.addOnPositiveButtonClickListener {
            textInputLayoutTime.error = null
            val hour = picker.hour
            val minute = picker.minute
            val time = String.format("%02d:%02d", hour, minute)
            textInputLayoutTime.editText?.setText(time)
        }

        textInputLayoutTime.editText?.doAfterTextChanged { text ->
            if (text.toString().trim().length == 5) {
                val (hour, minute) = text.toString().trim().split(":")
                if (hour.length != 2 || minute.length != 2 || hour.toInt() > 23 || hour.toInt() < 0 || minute.toInt() > 59 || minute.toInt() < 0) {
                    val errMsg = context.getString(R.string.error_msg_time_invalid_format)
                    textInputLayoutTime.error = errMsg
                } else {
                    textInputLayoutTime.error = null
                    textInputLayoutTime.clearFocus()
                }
            } else if (text.toString().trim().isNotEmpty()) {
                textInputLayoutTime.error = null
            }
        }
    }

    /**
     * Set EditText DatePickerDialog.
     * Use this function to set EditText DatePickerDialog and open Date Picker Dialog.
     * example in RoutineFormFragment.kt:
     * ```kotlin
     *      DatetimeAppManager().setEditTextDatePickerDialog(
     *            requireContext(),
     *            parentFragmentManager,
     *            binding.textInputLayoutStartDate,
     *            title = getString(R.string.select_start_date)
     *       )
     * ```
     *  @param context : Context
     *  @param parentFragmentManager : FragmentManager
     *  @param textInputLayoutDate : TextInputLayout
     *  @param title : String
     *  @param forwardOnly : Boolean
     *
     */
    fun setEditTextDatePickerDialog(
        context: Context,
        parentFragmentManager: FragmentManager,
        textInputLayoutDate: TextInputLayout,
        title: String = "Select date",
        forwardOnly : Boolean = false
    ) {
        val picker = datePickerBuilder(context, title, forwardOnly)

        textInputLayoutDate.editText?.isFocusable = false
        textInputLayoutDate.editText?.isClickable = true
        textInputLayoutDate.editText?.isCursorVisible = false


        textInputLayoutDate.editText?.setOnClickListener {
            picker.show(parentFragmentManager, "SessionFormFragment")
        }

        textInputLayoutDate.setEndIconOnClickListener {
            picker.show(parentFragmentManager, "SessionFormFragment")
        }

        picker.addOnPositiveButtonClickListener {
            textInputLayoutDate.error = null
            picker.selection?.let { epoc ->
                val selectedDate = Instant.ofEpochMilli(epoc).atZone(zoneLocalTimeId).toLocalDate()
                val formattedSelectedDate = selectedDate.format(DateTimeFormatter.ofPattern("dd LLLL yyyy"))
                textInputLayoutDate.editText?.setText(formattedSelectedDate)
            }
        }

    }



}