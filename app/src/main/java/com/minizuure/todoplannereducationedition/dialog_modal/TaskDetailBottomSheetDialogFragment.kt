package com.minizuure.todoplannereducationedition.dialog_modal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.minizuure.todoplannereducationedition.databinding.ModalAddNotesTaskBottomSheetDialogBinding
import com.minizuure.todoplannereducationedition.dialog_modal.preset.MinimumBottomSheetDialog
import com.minizuure.todoplannereducationedition.services.database.relations_table.SessionTaskProviderTable
import com.minizuure.todoplannereducationedition.services.database.relations_table.SessionTaskProviderViewModel
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineTable
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit


class TaskDetailBottomSheetDialogFragment(
    private val taskId : Long,
    private val sessionTaskProviderViewModel: SessionTaskProviderViewModel,
    private val routine: RoutineTable,
    private val currentDate : ZonedDateTime,
    private val isNextPlan : Boolean,
    private var title : String? = null,
    private var description : String? = null,
    private var todo : String? = null,
    private val onClickSaveAction : (Boolean, String, String?, ZonedDateTime, Map<String, ZonedDateTime>) -> Unit
) : MinimumBottomSheetDialog() {
    private val weeksDictionary : MutableMap<String, ZonedDateTime> = mutableMapOf()
    private var selectedWeek : ZonedDateTime = currentDate

    private val binding by lazy { ModalAddNotesTaskBottomSheetDialogBinding.inflate(layoutInflater) }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTitle()
        setupDescription()
        setupTodo()
        setupWeek()

        setupIsNextPlan()
        setupEditButton()
        setupSaveButton()
    }

    private fun setupIsNextPlan() {
        binding.textInputLayoutItemDateBottomSheet.visibility = if (isNextPlan) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

    private fun setupSaveButton() {
        binding.buttonSaveBottomSheet.setOnClickListener {
            selectedWeek = weeksDictionary[binding.textInputLayoutItemDateBottomSheet.editText?.text.toString()] ?: currentDate
            val name = binding.textInputLayoutItemNameBottomSheet.editText?.text.toString().trim()

            onClickSaveAction(
                isNextPlan,
                binding.textInputLayoutDescriptionBottomSheet.editText?.text.toString(),
                if (name == "") null else name,
                selectedWeek,
                weeksDictionary
            )
            dismiss()
        }
    }

    private fun setupEditButton() {
        if (description == null) {
            binding.buttonEditDescriptionBottomSheet.visibility = View.GONE
            binding.textInputLayoutDescriptionBottomSheet.visibility = View.VISIBLE
        } else {
            binding.buttonEditDescriptionBottomSheet.visibility = View.VISIBLE
            binding.textInputLayoutDescriptionBottomSheet.visibility = View.GONE
        }

        binding.buttonEditDescriptionBottomSheet.setOnClickListener {
            binding.buttonEditDescriptionBottomSheet.visibility = View.GONE
            binding.textInputLayoutDescriptionBottomSheet.visibility = View.VISIBLE
        }
    }

    private fun setupWeek() {
        CoroutineScope(Dispatchers.Main).launch {
            val meetsDictionary = withContext(Dispatchers.IO) { setMeetsDictionary() }

            if (!meetsDictionary) return@launch

            val adapter: ArrayAdapter<String> = object : ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                weeksDictionary.keys.toTypedArray()
            ) {
                override fun getFilter(): Filter {
                    return object : Filter() {
                        override fun performFiltering(constraint: CharSequence?): FilterResults {
                            return FilterResults().apply { values = weeksDictionary.keys.toTypedArray(); count = weeksDictionary.keys.size }
                        }

                        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                            if (results != null && results.count > 0) {
                                notifyDataSetChanged()
                            } else {
                                notifyDataSetInvalidated()
                            }
                        }
                    }
                }
            }

            (binding.textInputLayoutItemDateBottomSheet.editText
                    as? MaterialAutoCompleteTextView
                    )?.setAdapter(adapter)

            binding.textInputLayoutItemDateBottomSheet.editText?.apply {
                isFocusable = false
                isClickable = true
                isCursorVisible = false
            }


            binding.textInputLayoutItemDateBottomSheet.editText?.setText(weeksDictionary.keys.first())


        }


    }

    private suspend fun setMeetsDictionary() : Boolean {
        return coroutineScope {
            val sessionTaskProviders = withContext(Dispatchers.IO){
                sessionTaskProviderViewModel.getByTaskId(taskId)
            }

            return@coroutineScope if (sessionTaskProviders.first().isRescheduled) {
                setMeetsDictionaryRescheduled(sessionTaskProviders)
            } else {
                setMeetsDictionaryNotRescheduled()
            }
        }

    }

    private suspend fun setMeetsDictionaryRescheduled(sessionTaskProviders: List<SessionTaskProviderTable>) : Boolean {
        return coroutineScope {
            this.launch(Dispatchers.IO) {
                Log.d("TaskDetailBottomSheet", "sessionTaskProviders: $sessionTaskProviders with current date: $currentDate")
                val possibleWeeksMeets = mutableListOf<ZonedDateTime>()

                for (item in sessionTaskProviders) {
                    val startDate =
                        DatetimeAppManager(item.rescheduledDateStart!!).selectedDetailDatetimeISO
                    val endDate =
                        DatetimeAppManager(item.rescheduledDateEnd!!).selectedDetailDatetimeISO

                    if (currentDate.isAfter(endDate)) continue

                    val taskStartDate = getFirstTaskInWeek(item, startDate, endDate) ?: continue


                    val meets = ChronoUnit.WEEKS.between(taskStartDate, endDate).toInt()

                    for (i in 0..meets) {
                        val dateTime =
                            DatetimeAppManager(taskStartDate.plusWeeks(i.toLong())).selectedDetailDatetimeISO

                        if (dateTime.isBefore(currentDate)) continue

                        possibleWeeksMeets.add(dateTime)
                    }


                }

                possibleWeeksMeets.sort()

                for (i in possibleWeeksMeets.indices) {
                    val dateTime = DatetimeAppManager(possibleWeeksMeets[i]).toReadable()
                    if (i == 0) {
                        weeksDictionary["Next meet | $dateTime"] = possibleWeeksMeets[i]
                        continue
                    }
                    weeksDictionary["In $i meets | $dateTime"] = possibleWeeksMeets[i]
                }
            }


            return@coroutineScope true
        }
    }

    private fun getFirstTaskInWeek(sessionTaskProviderTable: SessionTaskProviderTable, startDate: ZonedDateTime, endDate: ZonedDateTime): ZonedDateTime? {
        val daysInterval = ChronoUnit.DAYS.between(startDate, endDate).toInt()

        for (i in 0..daysInterval) {
            val date = DatetimeAppManager(startDate.plusDays(i.toLong())).selectedDetailDatetimeISO
            val dayId = DatetimeAppManager(date).getTodayDayId()

            if (sessionTaskProviderTable.indexDay == dayId) {
                return date
            }
        }

        return null
    }

    private suspend fun setMeetsDictionaryNotRescheduled() : Boolean {

        return coroutineScope {
            val dateEnd = DatetimeAppManager(routine.date_end).selectedDetailDatetimeISO
            if (currentDate.isAfter(dateEnd)) return@coroutineScope true

            val weeks = ChronoUnit.WEEKS.between(currentDate, dateEnd).toInt()

            this.launch(Dispatchers.IO) {
                for (i in 1..weeks) {
                    val dateTime = DatetimeAppManager(currentDate.plusWeeks(i.toLong())).toReadable()
                    if (i == 1) {
                        weeksDictionary["Next meet"] = DatetimeAppManager(currentDate.plusWeeks(i.toLong())).selectedDetailDatetimeISO
                        continue
                    }
                    weeksDictionary["In $i meets | $dateTime"] = DatetimeAppManager(currentDate.plusWeeks(i.toLong())).selectedDetailDatetimeISO
                }
            }

            return@coroutineScope true
        }
    }


    private fun setupTodo() {
        todo?.let {
            binding.textInputLayoutItemNameBottomSheet.editText?.setText(it)
        }
    }

    private fun setupDescription() {
        description?.let {
            binding.textInputLayoutDescriptionBottomSheet.editText?.setText(it)
        }
    }

    private fun setupTitle() {
        title?.let {
            binding.textViewTitleBottomSheet.text = it
        }
    }

}