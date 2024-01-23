package com.minizuure.todoplannereducationedition.first_layer.reschedule

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.ToDoPlannerApplication
import com.minizuure.todoplannereducationedition.databinding.FragmentRescheduleBinding
import com.minizuure.todoplannereducationedition.dialog_modal.GlobalBottomSheetDialogFragment
import com.minizuure.todoplannereducationedition.dialog_modal.adapter.GlobalAdapter
import com.minizuure.todoplannereducationedition.dialog_modal.model.DaysOfWeekImpl
import com.minizuure.todoplannereducationedition.dialog_modal.model_interfaces.GlobalMinimumInterface
import com.minizuure.todoplannereducationedition.dialog_modal.preset.MinimumBottomSheetDialog
import com.minizuure.todoplannereducationedition.first_layer.TaskManagementActivity
import com.minizuure.todoplannereducationedition.services.customtextfield.CustomTextField
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModel
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.session.SessionTable
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModel
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.task.TaskViewModel
import com.minizuure.todoplannereducationedition.services.database.task.TaskViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.temp.RescheduleFormViewModel
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager
import com.minizuure.todoplannereducationedition.services.errormsgs.ErrorMsgManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.temporal.ChronoUnit


class RescheduleFragment : Fragment() {
    val args : RescheduleFragmentArgs by navArgs()
    private val rescheduleFormViewModel : RescheduleFormViewModel by activityViewModels()
    private lateinit var routineViewModel : RoutineViewModel
    private lateinit var sessionViewModel: SessionViewModel
    private lateinit var taskViewModel : TaskViewModel

    private val weeksDictionary : MutableMap<String, Int> = mutableMapOf()

    private val binding by lazy {
        FragmentRescheduleBinding.inflate(layoutInflater)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rescheduleFormViewModel.reset()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as TaskManagementActivity).setToolbarTitle(this)
        setupViewModelFactory()

        setupSelectedDatePicker()
        setupRescheduleDatePicker()
        setupStartTimePicker()
        setSessionDropDown()
        setupSwitch()

        setupErrorMessages()
    }

    private fun setupSwitch() {
        binding.switchRescheduleOption.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.rescheudleOption.text = getString(R.string.reschedule_all_following_meet)
            } else {
                binding.rescheudleOption.text = getString(R.string.reschedule_option_only_this_time)
            }
        }
    }


    private fun setSessionDropDown() {
        CustomTextField(requireContext()).setDropdownTextField(
            binding.textInputLayoutSelectSession,
            onClick = spinnerSelectSessionOnClick()
        )

        rescheduleFormViewModel.session.observe(viewLifecycleOwner) {
            rescheduleFormViewModel.setIsCustomSession(false)
            if (it != null) {
                val text = "${it.timeStart} - ${it.timeEnd}"
                binding.textInputLayoutSelectSession.editText?.setText(text)
                clearSessionError()
            } else {
                binding.textInputLayoutSelectSession.editText?.setText("")
            }
        }
    }

    private fun clearSessionError() {
        binding.textInputLayoutSelectSession.error = null
        binding.textInputLayoutSelectSession.isErrorEnabled = false
    }

    private fun spinnerSelectSessionOnClick(): () -> Unit = {
        lifecycleScope.launch {
            Log.d("RescheduleFragment", "spinnerSelectSessionOnClicked, isDialogOpen: ${MinimumBottomSheetDialog.isDialogOpen}")

            if (MinimumBottomSheetDialog.isDialogOpen) return@launch


            if (rescheduleFormViewModel.getDay() == null) {
                Log.e("RescheduleFragment", "spinnerSelectSessionOnClick: day is null")
                val errorMsg = getString(R.string.error_no_day_selected)
                binding.textInputLayoutSelectSession.error = errorMsg
                return@launch
            }

            val selectedDayId = rescheduleFormViewModel.getDay()!!.id

            val sessionsFiltered = withContext(Dispatchers.IO) {
                sessionViewModel.getSessionsForRoutine(
                    args.routineId,
                    isCustomSessionIncluded = false
                )
            }.filter { session ->
                session.selectedDays[selectedDayId.toInt()] == '1'
            }

            val sessionsAdapter = GlobalAdapter(
                useIndexes = true,
                useCustomName = true,
                customNameLogic = { session ->
                    if (session is SessionTable) {
                        return@GlobalAdapter "${session.timeStart} - ${session.timeEnd}"
                    } else {
                        Log.wtf("RescheduleFragment", "onClickSessionItem: it is not SessionTable")
                        return@GlobalAdapter session.title
                    }
                }
            )
            val bottomSheet = GlobalBottomSheetDialogFragment(
                title = getString(R.string.default_select_session),
                globalAdapter = sessionsAdapter,
                useAdditionalButton = true,
                additionalButtonText = getString(R.string.custom_session),
                additionalButtonLogic = customSessionLogic()
            )
            sessionsAdapter.onClickAction = onClickSessionItem(bottomSheet)

            sessionsAdapter.submitList(sessionsFiltered)

            bottomSheet.show(parentFragmentManager, "select_session_bottom_sheet")

        }
    }

    private fun onClickSessionItem(bottomSheet: GlobalBottomSheetDialogFragment): (GlobalMinimumInterface) -> Unit = {
        rescheduleFormViewModel.setIsCustomSession(false)
        if (it is SessionTable) {
            rescheduleFormViewModel.setSession(it)
        } else {
            Log.wtf("RescheduleFragment", "onClickSessionItem: it is not SessionTable")
        }
        bottomSheet.closeDialog()
    }

    private fun customSessionLogic(): () -> Unit = {
        rescheduleFormViewModel.setSession(null)
        rescheduleFormViewModel.setIsCustomSession(true)
        val text = getString(R.string.custom_session)
        binding.textInputLayoutSelectSession.editText?.setText(text)
    }


    private fun setupRescheduleDatePicker() {
        DatetimeAppManager().setEditTextDatePickerDialog(
            requireContext(),
            parentFragmentManager,
            binding.textInputLayoutAlternativeReschedule,
            forwardOnly = true,
            customSuccessAction = {
                val selectedDate = binding.textInputLayoutAlternativeReschedule.editText?.text.toString()
                val iso8601 = DatetimeAppManager().convertReadableDateToIso8601(selectedDate)
                val datetimeManager = DatetimeAppManager(iso8601)
                val daysOfWeekImpl = DaysOfWeekImpl(
                    id = datetimeManager.getTodayDayId().toLong(),
                    title = datetimeManager.getTodayDayName()
                )
                rescheduleFormViewModel.setDay(daysOfWeekImpl)
                clearSessionError()
            }
        )
    }

    private fun setupSelectedDatePicker() {
        lifecycleScope.launch {
            Log.d("RescheduleFragment", "setupSelectedDatePicker: program reached")

            val routine = withContext(Dispatchers.IO){ routineViewModel.getById(args.routineId) } ?: return@launch
            val dateEnd = DatetimeAppManager(routine.date_end).selectedDetailDatetimeISO

            val currentDate = args.selectedDatetimeDetailIso.zoneDateTime

            Log.d("RescheduleFragment", "setupSelectedDatePicker: $currentDate")

            if (currentDate.isAfter(dateEnd)) return@launch

            val weeks = ChronoUnit.WEEKS.between(currentDate, dateEnd).toInt()

            for (i in 0..weeks) {
                val dateTime = DatetimeAppManager(currentDate.plusWeeks(i.toLong())).toReadable()
                if (i == 1) {
                    weeksDictionary["Next week | $dateTime"] = i
                    continue
                } else if (i == 0) {
                    weeksDictionary["This week | $dateTime"] = 0
                    continue
                }
                weeksDictionary[ "In $i weeks | $dateTime"] = i
            }

            (binding.textInputLayoutSelectedReschedule.editText
                    as? MaterialAutoCompleteTextView
                    )?.setSimpleItems(weeksDictionary.keys.toTypedArray())

            binding.textInputLayoutSelectedReschedule.editText?.apply {
                isFocusable = false
                isClickable = true
                isCursorVisible = false
            }


            binding.textInputLayoutSelectedReschedule.editText?.setText(weeksDictionary.keys.first())

            Log.d("RescheduleFragment", "setupSelectedDatePicker: $weeksDictionary")
        }
    }

    private fun setupViewModelFactory() {
        val app : ToDoPlannerApplication = (requireActivity().application as ToDoPlannerApplication)

        val routineFactory = RoutineViewModelFactory(app.appRepository)
        routineViewModel = ViewModelProvider(requireActivity(), routineFactory)[RoutineViewModel::class.java]

        val sessionFactory = SessionViewModelFactory(app.appRepository)
        sessionViewModel = ViewModelProvider(requireActivity(), sessionFactory)[SessionViewModel::class.java]

        val taskFactory = TaskViewModelFactory(app.appRepository)
        taskViewModel = ViewModelProvider(requireActivity(), taskFactory)[TaskViewModel::class.java]

    }

    private fun setupStartTimePicker() {
        DatetimeAppManager().setEditTextTimePickerDialog(
            requireContext(),
            parentFragmentManager,
            binding.textInputLayoutStartTime,
            customSuccessAction = {
                val startTime = binding.textInputLayoutStartTime.editText?.text.toString()
                rescheduleFormViewModel.setTimeStart(startTime)
            }

        )

        DatetimeAppManager().setEditTextTimePickerDialog(
            requireContext(),
            parentFragmentManager,
            binding.textInputLayoutEndTime,
            customSuccessAction = {
                val endTime = binding.textInputLayoutEndTime.editText?.text.toString()
                rescheduleFormViewModel.setTimeEnd(endTime)
            }
        )

        setCustomSessionObserver()
    }

    private fun setCustomSessionObserver() {
        rescheduleFormViewModel.isCustomSession.observe(viewLifecycleOwner) { isCustomeSession ->
            if (isCustomeSession) {
                binding.groupCustomSession.visibility = View.VISIBLE

                if (rescheduleFormViewModel.isObserverActive) return@observe
                rescheduleFormViewModel.isObserverActive = true

                rescheduleFormViewModel.timeStart.observe(viewLifecycleOwner) {
                    it?.let {
                        lifecycleScope.launch {
                            val isActive = rescheduleFormViewModel.getIsCustomSession()

                            if (isActive) {
                                val timeEnd = rescheduleFormViewModel.getTimeEnd()

                                if (timeEnd == null || timeEnd == "") return@launch

                                checkTimeInterval(it, timeEnd)
                            }

                        }
                    }

                }

                rescheduleFormViewModel.timeEnd.observe(viewLifecycleOwner) {
                    it?.let {
                        lifecycleScope.launch {
                            val isActive = rescheduleFormViewModel.getIsCustomSession()

                            if (isActive) {
                                val timeStart = rescheduleFormViewModel.getTimeStart()

                                if(timeStart == null || timeStart == "") return@launch

                                checkTimeInterval(timeStart, it)
                            }

                        }
                    }
                }
            } else {
                binding.groupCustomSession.visibility = View.GONE

                if (rescheduleFormViewModel.isObserverActive) {
                    rescheduleFormViewModel.timeStart.removeObservers(viewLifecycleOwner)
                    rescheduleFormViewModel.timeEnd.removeObservers(viewLifecycleOwner)
                    rescheduleFormViewModel.isObserverActive = false
                }
            }
        }

    }

    private fun checkTimeInterval(timeStart: String, timeEnd: String) {
        val startTime = DatetimeAppManager().convertStringTimeToMinutes(timeStart)
        val endTime = DatetimeAppManager().convertStringTimeToMinutes(timeEnd)

        if (startTime >= endTime) {
            val errorMsg = getString(R.string.error_time_interval)
            binding.textInputLayoutEndTime.error = errorMsg
        } else {
            binding.textInputLayoutEndTime.error = null
        }
    }

    private fun setupErrorMessages() {
        ErrorMsgManager().setErrorMessages(
            requireContext(),
            binding.teksInputLayoutLocation,
            binding.teksInputLayoutLocation.editText,
            "20 characters max"
        )
    }

}