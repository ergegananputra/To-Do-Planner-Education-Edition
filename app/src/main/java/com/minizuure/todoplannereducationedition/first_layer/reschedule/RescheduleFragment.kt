package com.minizuure.todoplannereducationedition.first_layer.reschedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.ToDoPlannerApplication
import com.minizuure.todoplannereducationedition.databinding.FragmentRescheduleBinding
import com.minizuure.todoplannereducationedition.first_layer.TaskManagementActivity
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModel
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModel
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.task.TaskViewModel
import com.minizuure.todoplannereducationedition.services.database.task.TaskViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.temp.RescheduleFormViewModel
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager
import com.minizuure.todoplannereducationedition.services.errormsgs.ErrorMsgManager
import kotlinx.coroutines.launch


class RescheduleFragment : Fragment() {
    val args : RescheduleFragmentArgs by navArgs()
    private val rescheduleFormViewModel : RescheduleFormViewModel by activityViewModels()
    private lateinit var routineViewModel : RoutineViewModel
    private lateinit var sessionViewModel: SessionViewModel
    private lateinit var taskViewModel : TaskViewModel

    private val binding by lazy {
        FragmentRescheduleBinding.inflate(layoutInflater)
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

        setupStartTimePicker()
        setupErrorMessages()
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