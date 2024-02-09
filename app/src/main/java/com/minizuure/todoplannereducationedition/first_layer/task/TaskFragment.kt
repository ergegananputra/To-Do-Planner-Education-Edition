package com.minizuure.todoplannereducationedition.first_layer.task

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.ToDoPlannerApplication
import com.minizuure.todoplannereducationedition.databinding.FragmentTaskBinding
import com.minizuure.todoplannereducationedition.dialog_modal.GlobalBottomSheetDialogFragment
import com.minizuure.todoplannereducationedition.dialog_modal.adapter.GlobalAdapter
import com.minizuure.todoplannereducationedition.dialog_modal.model.DaysOfWeekImpl
import com.minizuure.todoplannereducationedition.dialog_modal.model_interfaces.GlobalMinimumInterface
import com.minizuure.todoplannereducationedition.dialog_modal.preset.MinimumBottomSheetDialog.Companion.isDialogOpen
import com.minizuure.todoplannereducationedition.first_layer.TaskManagementActivity
import com.minizuure.todoplannereducationedition.services.customtextfield.CustomTextField
import com.minizuure.todoplannereducationedition.services.database.DEFAULT_SESSION_ID
import com.minizuure.todoplannereducationedition.services.database.DEFAULT_TASK_ID
import com.minizuure.todoplannereducationedition.services.database.relations_table.SessionTaskProviderTable
import com.minizuure.todoplannereducationedition.services.database.relations_table.SessionTaskProviderViewModel
import com.minizuure.todoplannereducationedition.services.database.relations_table.SessionTaskProviderViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineTable
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModel
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.session.SessionTable
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModel
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.task.TaskTable
import com.minizuure.todoplannereducationedition.services.database.task.TaskViewModel
import com.minizuure.todoplannereducationedition.services.database.task.TaskViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.temp.TaskFormViewModel
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager
import com.minizuure.todoplannereducationedition.services.errormsgs.ErrorMsgManager
import com.minizuure.todoplannereducationedition.services.preferences.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ARG_TASK_ID = "com.minizuure.todoplannereducationedition.first_layer.task.taskId"
class TaskFragment : Fragment() {
    val args : TaskFragmentArgs by navArgs()
    private val taskFormViewModel : TaskFormViewModel by activityViewModels()
    private lateinit var routineViewModel : RoutineViewModel
    private lateinit var sessionViewModel: SessionViewModel
    private lateinit var taskViewModel : TaskViewModel
    private lateinit var sessionTaskProviderViewModel: SessionTaskProviderViewModel


    private val binding by lazy {
        FragmentTaskBinding.inflate(layoutInflater)
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

        setupDropdownTextField()
        setupTimePicker()
        setupCommunitySwitch()
        setupErrorMessages()

        setupSaveButton()

        loadData()
    }

    private fun loadData() {
        lifecycleScope.launch(Dispatchers.Main) {
            val taskAndSessionJoin = withContext(Dispatchers.IO) {
                taskViewModel.getTaskAndSessionJoinByProviderPrimaryKeys(
                    indexDay = args.indexDay,
                    taskId = args.taskId,
                    sessionId = args.sessionId,
                    paramDateIso8601 = ""
                )
            } ?: run {
                //TODO : Hapus ini
                Log.e("TaskFragment", "loadData: taskAndSessionJoin is null")
                val text = getString(R.string.you_have_not_set_a_default_routine)
                Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
                return@launch
            }

            val session = SessionTable(
                id = taskAndSessionJoin.fkSessionId,
                title = taskAndSessionJoin.sessionTitle,
                timeStart = taskAndSessionJoin.sessionTimeStart,
                timeEnd = taskAndSessionJoin.sessionTimeEnd,
                selectedDays = taskAndSessionJoin.sessionSelectedDays,
                fkRoutineId = taskAndSessionJoin.sessionFkRoutineId,
                isCustomSession = taskAndSessionJoin.sessionIsCustomSession
            )


            // title
            binding.taskInputLayoutTitle.editText?.setText(taskAndSessionJoin.title)

            // day
            val dayName = DatetimeAppManager().dayNameFromDayId(taskAndSessionJoin.indexDay)
            taskFormViewModel.setDay(DaysOfWeekImpl(taskAndSessionJoin.indexDay.toLong(), dayName))
            binding.textInputLayoutSelectDay.editText?.setText(dayName)

            // is Custom Session
            taskFormViewModel.setIsCustomSession(taskAndSessionJoin.sessionIsCustomSession)

            // session
            taskFormViewModel.setSession(session)

            // time start
            taskAndSessionJoin.sessionTimeStart.let { taskFormViewModel.setTimeStart(it) }

            // time end
            taskAndSessionJoin.sessionTimeEnd.let { taskFormViewModel.setTimeEnd(it) }

            // location
            binding.teksInputLayoutLocation.editText?.setText(taskAndSessionJoin.locationName)

            // location link
            binding.textInputLayoutMapsLink.editText?.setText(taskAndSessionJoin.locationAddress)


            val routine = withContext(Dispatchers.IO) {
                routineViewModel.getById(taskAndSessionJoin.sessionFkRoutineId)
            } ?: return@launch

            // routine template
            taskFormViewModel.setRoutineTemplate(routine)

        }
    }

    private fun setupSaveButton() {
        binding.buttonSaveTaskForm.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val taskId : Long = args.taskId
                val title : String = binding.taskInputLayoutTitle.editText?.text.toString().trim()
                val indexDay : Int? = taskFormViewModel.getDay()?.getId()
                val sessionId : Long = taskFormViewModel.getSession()?.id ?: DEFAULT_SESSION_ID
                val isCustomSession: Boolean = taskFormViewModel.getIsCustomSession()
                val customTimeStart: String? = taskFormViewModel.getTimeStart()
                val customTimeEnd: String? = taskFormViewModel.getTimeEnd()
                val location: String = binding.teksInputLayoutLocation.editText?.text.toString().trim()
                val locationLink : String= binding.textInputLayoutMapsLink.editText?.text.toString().trim()
                val isCommunity: Boolean = binding.switchShareToCommunity.isChecked
                val communityId : String? = null // TODO: setup community id

                val isValid = withContext(Dispatchers.Main) {
                    validateTaskForm(
                        title,
                        indexDay,
                        sessionId,
                        isCustomSession,
                        customTimeStart,
                        customTimeEnd,
                        isCommunity,
                        communityId
                    )
                }

                if (!isValid) return@launch

                if (taskId == DEFAULT_TASK_ID) {
                    // Create New Task
                    createNewTask(
                        title,
                        indexDay!!,
                        sessionId,
                        isCustomSession,
                        customTimeStart,
                        customTimeEnd,
                        location,
                        locationLink,
                        isCommunity,
                        communityId
                    )

                } else {
                    // Update Existing Task
                    updateTask(
                        taskId,
                        title,
                        indexDay!!,
                        sessionId,
                        isCustomSession,
                        customTimeStart,
                        customTimeEnd,
                        location,
                        locationLink,
                        isCommunity,
                        communityId
                    )
                }


                this.launch(Dispatchers.Main) {
                    (requireActivity() as TaskManagementActivity).onSupportNavigateUp()
                }
            }


        }

    }

    private fun validateTaskForm(
        title: String,
        indexDay: Int?,
        sessionId: Long?,
        customSession: Boolean,
        customTimeStart: String?,
        customTimeEnd: String?,
        community: Boolean,
        communityId: String?
    ) : Boolean {
        if (title.isEmpty()) {
            val errorMsg = getString(R.string.error_msg_title_empty)
            binding.taskInputLayoutTitle.error = errorMsg
            return false
        }

        if (indexDay == null) {
            val errorMsg = getString(R.string.error_msg_day_empty)
            binding.textInputLayoutSelectDay.error = errorMsg
            return false
        }

        if (sessionId == DEFAULT_SESSION_ID && !customSession) {
            val errorMsg = getString(R.string.error_msg_session_empty)
            binding.textInputLayoutSelectSession.error = errorMsg
            return false
        }

        if (!customSession) return true

        if (customTimeStart == null || customTimeStart == "") {
            val errorMsg = getString(R.string.error_msg_time_is_not_valid)
            binding.textInputLayoutStartTime.error = errorMsg
            return false
        }

        if (customTimeEnd == null || customTimeEnd == "") {
            val errorMsg = getString(R.string.error_msg_time_is_not_valid)
            binding.textInputLayoutEndTime.error = errorMsg
            return false
        }

        if (!community) return true

        if (communityId == null || communityId == "") {
            val errorMsg = getString(R.string.error_msg_community_empty)
            Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private suspend fun updateTask(
        taskId: Long,
        title: String,
        indexDay: Int,
        sessionId: Long,

        customSession: Boolean,
        customTimeStart: String?,
        customTimeEnd: String?,

        location: String,
        locationLink: String,

        community: Boolean,
        communityId: String?
    ) {
        withContext(Dispatchers.IO) {
            taskViewModel.update(
                TaskTable(
                    id = taskId,
                    title = title,
                    isSharedToCommunity = community,
                    communityId = communityId,
                )
            )

            sessionTaskProviderViewModel.updateLocationByPrimaryKeys(
                indexDay = indexDay,
                taskId = taskId,
                sessionId = sessionId,
                location = location,
                locationLink = locationLink
            )

            if (customSession) {
                sessionViewModel.updateCustomSession(
                    sessionId,
                    true,
                    customTimeStart!!,
                    customTimeEnd!!
                )
            }
        }
    }

    private suspend fun createCustomSession(indexDay: Int, customTimeStart: String, customTimeEnd: String) : Long{
        val routine = withContext(Dispatchers.IO) { taskFormViewModel.getRoutineTemplate() } ?: return DEFAULT_SESSION_ID

        val selectedDay = "0000000"
        val newSelectedDay = selectedDay.mapIndexed { index, char ->
            if (index == indexDay) '1' else char
        }.joinToString("")

        return sessionViewModel.insert(
            title = "Custom",
            timeStart = customTimeStart,
            timeEnd = customTimeEnd,
            selectedDays = newSelectedDay,
            fkRoutineId = routine.id,
            isCustomSession = true

        )
    }

    private suspend fun createNewTask(
        title: String,
        indexDay: Int,
        formSessionId: Long,

        customSession: Boolean,
        customTimeStart: String?,
        customTimeEnd: String?,

        location: String,
        locationLink: String?,

        community: Boolean,
        communityId: String?
    ) {
        val taskId = withContext(Dispatchers.IO) {
            taskViewModel.insert(
                title = title,
                isSharedToCommunity = community,
                communityId = communityId
            )
        }

        val sessionId = if (customSession) {
            createCustomSession(indexDay, customTimeStart!!, customTimeEnd!!)
        } else {
            formSessionId
        }

        withContext(Dispatchers.IO) {
            sessionTaskProviderViewModel.insert(
                SessionTaskProviderTable(
                    indexDay = indexDay,
                    fkTaskId = taskId,
                    fkSessionId = sessionId,
                    locationName = location,
                    locationLink = locationLink
                )
            )
        }

        if (taskId == DEFAULT_TASK_ID) {
            Log.e("TaskFragment", "createNewTask: failed to insert task")
            Toast.makeText(requireContext(), getString(R.string.failed_to_create_task), Toast.LENGTH_SHORT).show()
            return
        } else {
            Log.d("TaskFragment", "createNewTask: success to insert task")
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

        val sessionTaskProviderFactory = SessionTaskProviderViewModelFactory(app.appRepository)
        sessionTaskProviderViewModel = ViewModelProvider(requireActivity(), sessionTaskProviderFactory)[SessionTaskProviderViewModel::class.java]

    }

    private fun setupCommunitySwitch() {
        // TODO: setup community switch
    }

    private fun setupDropdownTextField() {
        setRoutineTemplateDropDown()
        setDayDropDown()
        setSessionDropDown()
    }

    private fun setSessionDropDown() {
        CustomTextField(requireContext()).setDropdownTextField(
            binding.textInputLayoutSelectSession,
            onClick = spinnerSelectSessionOnClick()
        )

        taskFormViewModel.session.observe(viewLifecycleOwner) {
            taskFormViewModel.setIsCustomSession(false)
            if (it != null) {
                val text = "${it.timeStart} - ${it.timeEnd}"
                binding.textInputLayoutSelectSession.editText?.setText(text)
            } else {
                binding.textInputLayoutSelectSession.editText?.setText("")
            }
        }
    }

    private fun setDayDropDown() {
        CustomTextField(requireContext()).setDropdownTextField(
            binding.textInputLayoutSelectDay,
            onClick = spinnerSelectDayOnClick()
        )

        taskFormViewModel.day.observe(viewLifecycleOwner) {
            taskFormViewModel.setSession(null)
            clearDayError()
        }
    }

    private fun clearDayError() {
        binding.textInputLayoutSelectSession.error = null
    }

    private fun setRoutineTemplateDropDown() {
        lifecycleScope.launch {
            val routineTemplateId = UserPreferences(requireContext()).defaultRoutineId
            val routineTemplate = withContext(Dispatchers.IO) {
                routineViewModel.getById(routineTemplateId)
            }

            routineTemplate?.let { routineTemplateNonNull ->
                taskFormViewModel.setRoutineTemplate(routineTemplateNonNull)
            }

        }

        CustomTextField(requireContext()).setDropdownTextField(
            binding.textInputLayoutRoutineTemplate,
            onClick = spinnerRoutineTemplateOnClick()
        )

        taskFormViewModel.routineTemplate.observe(viewLifecycleOwner) {
            it?.let {
                binding.textInputLayoutRoutineTemplate.editText?.setText(it.title)
            }
        }
    }

    private fun spinnerSelectSessionOnClick(): () -> Unit = {
        lifecycleScope.launch {
            Log.d("TaskFragment", "spinnerSelectSessionOnClicked, isDialogOpen: $isDialogOpen")

            if (isDialogOpen) return@launch
            if (taskFormViewModel.getRoutineTemplate() == null) {
                Log.e("TaskFragment", "spinnerSelectSessionOnClick: routine is null")
                val errorMsg = getString(R.string.error_no_routine_template_selected)
                binding.textInputLayoutSelectSession.error = errorMsg
                return@launch
            }

            if (taskFormViewModel.getDay() == null) {
                Log.e("TaskFragment", "spinnerSelectSessionOnClick: day is null")
                val errorMsg = getString(R.string.error_no_day_selected)
                binding.textInputLayoutSelectSession.error = errorMsg
                return@launch
            }

            val selectedDayId = taskFormViewModel.getDay()!!.id

            val sessionsFiltered = withContext(Dispatchers.IO) {
                sessionViewModel.getSessionsForRoutine(
                    taskFormViewModel.getRoutineTemplate()!!.id,
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
                        Log.wtf("TaskFragment", "onClickSessionItem: it is not SessionTable")
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

    private fun customSessionLogic(): () -> Unit = {
        taskFormViewModel.setSession(null)
        taskFormViewModel.setIsCustomSession(true)
        val text = getString(R.string.custom_session)
        binding.textInputLayoutSelectSession.editText?.setText(text)
    }


    private fun onClickSessionItem(bottomSheet: GlobalBottomSheetDialogFragment): (GlobalMinimumInterface) -> Unit = {
        taskFormViewModel.setIsCustomSession(false)
        if (it is SessionTable) {
            taskFormViewModel.setSession(it)
        } else {
            Log.wtf("TaskFragment", "onClickSessionItem: it is not SessionTable")
        }
        bottomSheet.closeDialog()
    }

    private fun spinnerSelectDayOnClick(): () -> Unit = {
        lifecycleScope.launch {
            val routine = taskFormViewModel.getRoutineTemplate()

            if (isDialogOpen) return@launch
            if (routine == null) {
                Log.e("TaskFragment", "spinnerSelectDayOnClick: routine is null")
                val errorMsg = getString(R.string.error_no_routine_template_selected)
                binding.textInputLayoutSelectDay.error = errorMsg
                return@launch
            }
            val days = DatetimeAppManager().getAllDaysOfWeek()
                .mapIndexed { index, title ->
                    DaysOfWeekImpl(index.toLong(), title)
                }

            val selectDaysAdapter = GlobalAdapter(
                firstDiffrenetColor = true,
                startFromIndexZero = true
            )
            val bottomSheet = GlobalBottomSheetDialogFragment(
                title = getString(R.string.default_select_day),
                globalAdapter = selectDaysAdapter
            )
            selectDaysAdapter.onClickAction = onClickDayItem(bottomSheet)

            selectDaysAdapter.submitList(days)

            bottomSheet.show(parentFragmentManager, "select_days_bottom_sheet")

        }
    }

    private fun onClickDayItem(bottomSheet: GlobalBottomSheetDialogFragment): (GlobalMinimumInterface) -> Unit = {
        if (it is DaysOfWeekImpl) {
            binding.textInputLayoutSelectDay.editText?.setText(it.title)
            taskFormViewModel.setDay(it)
        } else {
            Log.wtf("TaskFragment", "onClickDayItem: it is not DaysOfWeekImpl")
        }
        bottomSheet.closeDialog()
    }

    private fun spinnerRoutineTemplateOnClick(): () -> Unit = {
        lifecycleScope.launch(Dispatchers.Main) {
            if (isDialogOpen) return@launch

            val routines = withContext(Dispatchers.IO) {
                routineViewModel.getAll()
            }

            val routineTemplateAdapter = GlobalAdapter(useIndexes = true)
            val bottomSheet = GlobalBottomSheetDialogFragment(
                title = getString(R.string.default_select_routines_template),
                globalAdapter = routineTemplateAdapter
            )
            routineTemplateAdapter.onClickAction = onClickRoutineItem(bottomSheet)

            routineTemplateAdapter.submitList(routines)

            bottomSheet.show(parentFragmentManager, "routine_template_bottom_sheet")

        }
    }

    private fun onClickRoutineItem(bottomSheet: GlobalBottomSheetDialogFragment): (GlobalMinimumInterface) -> Unit = {
        if (it is RoutineTable) {
            binding.textInputLayoutRoutineTemplate.editText?.setText(it.title)
            taskFormViewModel.setRoutineTemplate(it)

        } else {
            Log.wtf("TaskFragment", "onClickRoutineItem: it is not RoutineTemplateImpl")
        }
        bottomSheet.closeDialog()
    }

    private fun setupTimePicker() {
        DatetimeAppManager().setEditTextTimePickerDialog(
            requireContext(),
            parentFragmentManager,
            binding.textInputLayoutStartTime,
            customSuccessAction = {
                val startTime = binding.textInputLayoutStartTime.editText?.text.toString()
                taskFormViewModel.setTimeStart(startTime)
            }
        )

        DatetimeAppManager().setEditTextTimePickerDialog(
            requireContext(),
            parentFragmentManager,
            binding.textInputLayoutEndTime,
            customSuccessAction = {
                val endTime = binding.textInputLayoutEndTime.editText?.text.toString()
                taskFormViewModel.setTimeEnd(endTime)
            }
        )

        setCustomSessionObserver()
    }

    private fun setCustomSessionObserver() {
        taskFormViewModel.isCustomSession.observe(viewLifecycleOwner) { isCustomeSession ->
            if (isCustomeSession) {
                binding.groupCustomSession.visibility = View.VISIBLE

                if (taskFormViewModel.isObserverActive) return@observe
                taskFormViewModel.isObserverActive = true

                taskFormViewModel.timeStart.observe(viewLifecycleOwner) {
                    it?.let {
                        lifecycleScope.launch {
                            val isActive = taskFormViewModel.getIsCustomSession()

                            if (isActive) {
                                val timeEnd = taskFormViewModel.getTimeEnd()

                                if (timeEnd == null || timeEnd == "") return@launch

                                checkTimeInterval(it, timeEnd)
                            }

                        }
                    }

                }

                taskFormViewModel.timeEnd.observe(viewLifecycleOwner) {
                    it?.let {
                        lifecycleScope.launch {
                            val isActive = taskFormViewModel.getIsCustomSession()

                            if (isActive) {
                                val timeStart = taskFormViewModel.getTimeStart()

                                if(timeStart == null || timeStart == "") return@launch

                                checkTimeInterval(timeStart, it)
                            }

                        }
                    }
                }
            } else {
                binding.groupCustomSession.visibility = View.GONE

                if (taskFormViewModel.isObserverActive) {
                    taskFormViewModel.timeStart.removeObservers(viewLifecycleOwner)
                    taskFormViewModel.timeEnd.removeObservers(viewLifecycleOwner)
                    taskFormViewModel.isObserverActive = false
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param taskId : Long (default = -1L)
         * @return A new instance of fragment TaskFragment.
         */
        @JvmStatic
        fun newInstance(taskId: Long = -1L) =
            TaskFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_TASK_ID, taskId)
                }
            }
    }

}