package com.minizuure.todoplannereducationedition.dialog_modal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.minizuure.todoplannereducationedition.databinding.ModalAddNotesTaskBottomSheetDialogBinding
import com.minizuure.todoplannereducationedition.dialog_modal.preset.MinimumBottomSheetDialog
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineTable
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit


class TaskDetailBottomSheetDialogFragment(
    private val taskId : Long,
    private val routine: RoutineTable,
    private val currentDate : ZonedDateTime,
    private val isNextPlan : Boolean,
    private var title : String? = null,
    private var description : String? = null,
    private var todo : String? = null,
    private val onClickSaveAction : (Boolean, String, String, Int, Map<Int, String>) -> Unit
) : MinimumBottomSheetDialog() {
    private val weeksDictionary : MutableMap<Int, String> = mutableMapOf()
    private var selectedWeek : Int = 0

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
            selectedWeek =
                weeksDictionary.values.indexOf(
                    binding.textInputLayoutItemDateBottomSheet.editText?.text.toString()
                ) + 1


            onClickSaveAction(
                isNextPlan,
                binding.textInputLayoutDescriptionBottomSheet.editText?.text.toString(),
                binding.textInputLayoutItemNameBottomSheet.editText?.text.toString(),
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
        val dateEnd = DatetimeAppManager(routine.date_end).selectedDetailDatetimeISO

        if (currentDate.isAfter(dateEnd)) return

        val weeks = ChronoUnit.WEEKS.between(currentDate, dateEnd).toInt()

        for (i in 1..weeks) {
            val dateTime = DatetimeAppManager(currentDate.plusWeeks(i.toLong())).toReadable()
            if (i == 1) {
                weeksDictionary[i] = "Next week"
                continue
            }
            weeksDictionary[i] = "In $i weeks | $dateTime"
        }

        (binding.textInputLayoutItemDateBottomSheet.editText
                    as? MaterialAutoCompleteTextView
                    )?.setSimpleItems(weeksDictionary.values.toTypedArray())

        binding.textInputLayoutItemDateBottomSheet.editText?.apply {
            isFocusable = false
            isClickable = true
            isCursorVisible = false
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