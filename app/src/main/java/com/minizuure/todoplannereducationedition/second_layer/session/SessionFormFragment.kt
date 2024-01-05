package com.minizuure.todoplannereducationedition.second_layer.session

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.databinding.FragmentSessionFormBinding
import com.minizuure.todoplannereducationedition.second_layer.RoutineManagementActivity
import com.minizuure.todoplannereducationedition.services.database.temp.RoutineFormViewModel
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SessionFormFragment : Fragment() {
    val args : SessionFormFragmentArgs by navArgs()
    private val routineFormViewModel : RoutineFormViewModel by activityViewModels()
    private val binding by lazy {
        FragmentSessionFormBinding.inflate(layoutInflater)
    }

    private val selectedDaysAnimator by lazy {
        ObjectAnimator.ofFloat(binding.textViewSelectedDaysLabel, View.ALPHA, 0.5f, 1f).apply {
            duration = 500
            repeatCount = 4
            repeatMode = ObjectAnimator.REVERSE
        }
    }

    private fun startAnimationSelectedDaysLabel() {
        lifecycleScope.launch {
            selectedDaysAnimator.start()
            delay(500 * 4 + 10)

            selectedDaysAnimator.cancel() // safe to cancel
            binding.textViewSelectedDaysLabel.alpha = 1f
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as RoutineManagementActivity).setToolbarTitle(this)
        if (args.sessionId != -1) {
            loadData()
        }

        setupSaveButton()
        setupSessionTitle()
        setupStartTimePicker()
        setupDaysTag()
    }

    private fun loadData() {
        binding.textInputLayoutSessionTitle.editText?.setText(args.title)
        binding.textInputLayoutStartTime.editText?.setText(args.startTime)
        binding.textInputLayoutEndTime.editText?.setText(args.endTime)

        val selectedDays = args.selectedDays
        val days = listOf(
            binding.buttonTagsSunday,
            binding.buttonTagsMonday,
            binding.buttonTagsTuesday,
            binding.buttonTagsWednesday,
            binding.buttonTagsThursday,
            binding.buttonTagsFriday,
            binding.buttonTagsSaturday,
        )
        selectedDays?.forEachIndexed { index, c ->
            days[index].isActivated = c == '1'
        }
    }

    private fun setupSessionTitle() {
        binding.textInputLayoutSessionTitle.editText?.doAfterTextChanged { text ->
            if (text.toString().trim().isNotEmpty()) {
                binding.textInputLayoutSessionTitle.error = null
            }
        }
    }

    private fun setupDaysTag() {
        with(binding) {
            buttonTagsSunday.setOnClickListener {
                buttonTagsSunday.isActivated = !buttonTagsSunday.isActivated
                clearEditTextFocus()
            }
            buttonTagsMonday.setOnClickListener {
                buttonTagsMonday.isActivated = !buttonTagsMonday.isActivated
                clearEditTextFocus()
            }
            buttonTagsTuesday.setOnClickListener {
                buttonTagsTuesday.isActivated = !buttonTagsTuesday.isActivated
                clearEditTextFocus()
            }
            buttonTagsWednesday.setOnClickListener {
                buttonTagsWednesday.isActivated = !buttonTagsWednesday.isActivated
                clearEditTextFocus()
            }
            buttonTagsThursday.setOnClickListener {
                buttonTagsThursday.isActivated = !buttonTagsThursday.isActivated
                clearEditTextFocus()
            }
            buttonTagsFriday.setOnClickListener {
                buttonTagsFriday.isActivated = !buttonTagsFriday.isActivated
                clearEditTextFocus()
            }
            buttonTagsSaturday.setOnClickListener {
                buttonTagsSaturday.isActivated = !buttonTagsSaturday.isActivated
                clearEditTextFocus()
            }
        }
    }

    private fun setupStartTimePicker() {
        DatetimeAppManager().setEditTextTimePickerDialog(
            requireContext(),
            parentFragmentManager,
            binding.textInputLayoutStartTime
        )

        DatetimeAppManager().setEditTextTimePickerDialog(
            requireContext(),
            parentFragmentManager,
            binding.textInputLayoutEndTime
        )
    }


    private fun setupSaveButton() {
        binding.buttonSaveRouteForm.setOnClickListener {
            clearEditTextFocus()

            val title = binding.textInputLayoutSessionTitle.editText?.text.toString().trim()
            val startTime = binding.textInputLayoutStartTime.editText?.text.toString()
            val endTime = binding.textInputLayoutEndTime.editText?.text.toString()
            val days = listOf(
                binding.buttonTagsSunday.isActivated,
                binding.buttonTagsMonday.isActivated,
                binding.buttonTagsTuesday.isActivated,
                binding.buttonTagsWednesday.isActivated,
                binding.buttonTagsThursday.isActivated,
                binding.buttonTagsFriday.isActivated,
                binding.buttonTagsSaturday.isActivated,
            )

            if(validateForm(title, startTime, endTime, days)) {
                return@setOnClickListener
            }



            val daysSelected = days.joinToString("") { if (it) "1" else "0" }

            // old routines always have id so it can store to database immediately
            if (args.newRoutine) {
                lifecycleScope.launch {
                    // check if session is new or old to determine if it should be added to temp session or updated
                    if (args.sessionId == -1) {
                        routineFormViewModel.addTempSession(
                            title = title,
                            startTime = startTime,
                            endTime = endTime,
                            daysSelected = daysSelected
                        )
                        navigateBackSuccess()
                    } else {
                        routineFormViewModel.updateTempSession(
                            id = args.sessionId,
                            title = title,
                            startTime = startTime,
                            endTime = endTime,
                            daysSelected = daysSelected
                        )
                        navigateBackSuccess()
                    }
                }
            } else {
                lifecycleScope.launch {
                    uploadToDatabase()
                    navigateBackSuccess()
                }
            }
        }
    }

    private fun clearEditTextFocus() {
        binding.textInputLayoutSessionTitle.clearFocus()
        binding.textInputLayoutStartTime.clearFocus()
        binding.textInputLayoutEndTime.clearFocus()
    }

    private fun validateForm(title: String, startTime: String, endTime: String, days: List<Boolean>) : Boolean {
        val notValid = true
        if (title == "") {
            val errMsg = getString(R.string.error_msg_title_empty)
            binding.textInputLayoutSessionTitle.error = errMsg
            return notValid
        }

        if (startTime == "") {
            val errMsg = getString(R.string.error_msg_start_time_empty)
            binding.textInputLayoutStartTime.error = errMsg
            return notValid
        }

        if (endTime == "") {
            val errMsg = getString(R.string.error_msg_end_time_empty)
            binding.textInputLayoutEndTime.error = errMsg
            return notValid
        }

        // check if at least one day is selected with check if all days is false
        if (days.all { it == false }) {
            val errMsg = getString(R.string.error_msg_no_days_selected_session_form)
            Toast.makeText(requireContext(), errMsg, Toast.LENGTH_SHORT).show()
            startAnimationSelectedDaysLabel()
            return notValid
        }

        return !notValid
    }

    private suspend fun uploadToDatabase() {
        // TODO: upload ke database
    }

    private fun navigateBackSuccess() {
        Toast.makeText(requireContext(), "Session saved", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }


}