package com.minizuure.todoplannereducationedition.first_layer.reschedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.minizuure.todoplannereducationedition.ToDoPlannerApplication
import com.minizuure.todoplannereducationedition.databinding.FragmentRescheduleBinding
import com.minizuure.todoplannereducationedition.first_layer.TaskManagementActivity
import com.minizuure.todoplannereducationedition.services.database.notes.NoteViewModel
import com.minizuure.todoplannereducationedition.services.database.notes.NoteViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModel
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModel
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.task.TaskViewModel
import com.minizuure.todoplannereducationedition.services.database.task.TaskViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.temp.RescheduleFormViewModel

/**
 * [RescheduleFragment] is used to reschedule a task.
 *
 *
 * TODO List :
 * 1.   Membuat logic untuk reschedule task. [setupSaveButton]
 *
 */
class RescheduleFragment : Fragment() {
    val args : RescheduleFragmentArgs by navArgs()
    private val rescheduleFormViewModel : RescheduleFormViewModel by activityViewModels()
    private lateinit var routineViewModel : RoutineViewModel
    private lateinit var sessionViewModel: SessionViewModel
    private lateinit var taskViewModel : TaskViewModel
    private lateinit var notesViewModel : NoteViewModel

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
        setupViewModelFactory()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as TaskManagementActivity).setToolbarTitle(this)

//
//        setupSelectedDatePicker()
//        setupRescheduleDatePicker()
//        setupStartTimePicker()
//        setSessionDropDown()
//        setupSwitch()
//
//        setupSaveButton()
//        setupErrorMessages()
    }

    private fun setupViewModelFactory() {
        val app : ToDoPlannerApplication = (requireActivity().application as ToDoPlannerApplication)

        val routineFactory = RoutineViewModelFactory(app.appRepository)
        routineViewModel = ViewModelProvider(requireActivity(), routineFactory)[RoutineViewModel::class.java]

        val sessionFactory = SessionViewModelFactory(app.appRepository)
        sessionViewModel = ViewModelProvider(requireActivity(), sessionFactory)[SessionViewModel::class.java]

        val taskFactory = TaskViewModelFactory(app.appRepository)
        taskViewModel = ViewModelProvider(requireActivity(), taskFactory)[TaskViewModel::class.java]

        val noteFactory = NoteViewModelFactory(app.appRepository)
        notesViewModel = ViewModelProvider(requireActivity(), noteFactory)[NoteViewModel::class.java]
    }

//    private fun setupSaveButton() {
//        /**
//         * TODO: membuat save button logic.
//         * 1.   Cek Switch, jika true maka reschedule semua task
//         *      yang ada di session tersebut setelah hari Task yang dipilih.
//         *
//         * 2.   Buat Task Duplicate dengan rentang waktu yang dipilih.
//         *
//         * 3.   Migrasi semua notes setelah hari Task yang dipilih ke Task Duplicate.
//         *
//         */
//
//        binding.buttonSaveRescheduleForm.setOnClickListener {
//            Log.d("RescheduleFragment", "setupSaveButton: clicked")
//            Toast.makeText(requireContext(), "Clicked: Unimplemented yet", Toast.LENGTH_SHORT).show()
//
//            lifecycleScope.launch(Dispatchers.IO) {
//                val taskId : Long = args.taskId
//                val title : String = args.taskTitle
//                val weekFromNow : Int = weeksDictionary[binding.textInputLayoutSelectedReschedule.editText?.text.toString()] ?: 0
//                val breakPointDate : String = binding.textInputLayoutAlternativeReschedule.editText?.text.toString().trim()
//                val indexDay : Int? = rescheduleFormViewModel.getDay()?.getId()
//                val sessionId : Long = rescheduleFormViewModel.getSession()?.id ?: DEFAULT_SESSION_ID
//                val isCustomSession : Boolean = rescheduleFormViewModel.getIsCustomSession()
//                val customTimeStart : String? = rescheduleFormViewModel.getTimeStart()
//                val customTimeEnd : String? = rescheduleFormViewModel.getTimeEnd()
//                val location : String = binding.teksInputLayoutLocation.editText?.text.toString().trim()
//                val locationLink : String = binding.textInputLayoutMapsLink.editText?.text.toString().trim()
//                val isRescheduleAllFollowingMeet : Boolean = binding.switchRescheduleOption.isChecked
//                // TODO: Set Community ID
//
//                val isValid = withContext(Dispatchers.Main) {
//                    validateForm(
//                        indexDay,
//                        isCustomSession,
//                        customTimeStart,
//                        customTimeEnd
//                    )
//                }
//
//                if (!isValid) return@launch
//
//                rescheduleTask(
//                    taskId,
//                    title,
//                    weekFromNow,
//                    DatetimeAppManager().convertReadableDateToIso8601(breakPointDate),
//                    indexDay!!,
//                    sessionId,
//                    isCustomSession,
//                    customTimeStart,
//                    customTimeEnd,
//                    location,
//                    locationLink,
//                    isRescheduleAllFollowingMeet,
//                    this.launch(Dispatchers.Main) {
//                        (requireActivity() as TaskManagementActivity).onSupportNavigateUp()
//                    }
//                )
//
//
//            }
//        }
//    }
//
//    private suspend fun rescheduleTask(
//        taskId: Long,
//        title: String,
//        weekFromNow: Int,
//        breakPointDateString: String,
//        indexDay: Int,
//        sessionId: Long,
//
//        isCustomSession: Boolean,
//        customTimeStart: String?,
//        customTimeEnd: String?,
//
//        location: String,
//        locationLink: String,
//        isRescheduleAllFollowingMeet: Boolean,
//
//        closeAction: Job
//    ) {
//        withContext(Dispatchers.IO) {
//            val originalTask = taskViewModel.getById(taskId) ?: return@withContext
//            val routine = routineViewModel.getById(args.routineId) ?: return@withContext
//
//            val breakPointDate = DatetimeAppManager(DatetimeAppManager(breakPointDateString).selectedDetailDatetimeISO, accuracyOnlyUpToDays = true).selectedDetailDatetimeISO
//            val currentDate = args.selectedDatetimeDetailIso.zoneDateTime.plusWeeks(weekFromNow.toLong())
//
//            val startDateZone : ZonedDateTime
//            val endDateZone : ZonedDateTime
//
//            if (originalTask.isRescheduled) {
//                startDateZone = DatetimeAppManager(originalTask.rescheduledTimeStart!!).selectedDetailDatetimeISO
//                endDateZone = DatetimeAppManager(originalTask.rescheduledTimeEnd!!).selectedDetailDatetimeISO
//            } else {
//                originalTask.isRescheduled = true
//                startDateZone = DatetimeAppManager(routine.date_start).selectedDetailDatetimeISO
//                endDateZone = DatetimeAppManager(routine.date_end).selectedDetailDatetimeISO
//            }
//
//            val selectedDays = "0000000".mapIndexed { index, _ ->
//                if (index == indexDay) '1' else '0'
//            }.joinToString("")
//
//            val newSessionId = if (isCustomSession) {
//                sessionViewModel.insert(
//                    title = "Reschedule Session | ${title.take(15)}",
//                    timeStart = customTimeStart!!,
//                    timeEnd = customTimeEnd!!,
//                    selectedDays = selectedDays,
//                    fkRoutineId = args.routineId,
//                    isCustomSession = true
//                )
//            } else {
//                sessionId
//            }
//
//            if (newSessionId == -1L) return@withContext
//
//            var result = false
//
//            if (isRescheduleAllFollowingMeet) {
//                val taskLeft = originalTask.copy(
//                    isRescheduled = true,
//                    rescheduledTimeStart = DatetimeAppManager(startDateZone, true).dateISO8601inString,
//                    rescheduledTimeEnd = DatetimeAppManager(breakPointDate.minusDays(1), true).dateISO8601inString,
//                )
//
//                val taskRight = TaskTable(
//                    title = originalTask.title,
//                    indexDay = indexDay,
//                    sessionId = newSessionId,
//                    locationName = location,
//                    locationAddress = locationLink,
//                    isRescheduled = true,
//                    rescheduledTimeStart = DatetimeAppManager(breakPointDate, true).dateISO8601inString,
//                    rescheduledTimeEnd = DatetimeAppManager(endDateZone, true).dateISO8601inString,
//                    isSharedToCommunity = originalTask.isSharedToCommunity,
//                    communityId = originalTask.communityId
//                )
//
//                taskViewModel.update(taskLeft)
//                val rightTaskId = taskViewModel.insert(taskRight)
//
//                result = migrateNotes(taskId, null, rightTaskId)
//            } else {
//                val taskLeft = originalTask.copy(
//                    isRescheduled = true,
//                    rescheduledTimeStart = DatetimeAppManager(startDateZone, true).dateISO8601inString,
//                    rescheduledTimeEnd = DatetimeAppManager(currentDate.minusWeeks(1), true).dateISO8601inString,
//                )
//
//                val taskCenter = TaskTable(
//                    title = originalTask.title,
//                    indexDay = indexDay,
//                    sessionId = newSessionId,
//                    locationName = location,
//                    locationAddress = locationLink,
//                    isRescheduled = true,
//                    rescheduledTimeStart = DatetimeAppManager(breakPointDate, true).dateISO8601inString,
//                    rescheduledTimeEnd = DatetimeAppManager(breakPointDate, true).dateISO8601inString,
//                    isSharedToCommunity = originalTask.isSharedToCommunity,
//                    communityId = originalTask.communityId
//                )
//
//                val taskRight = taskLeft.copy(
//                    id = 0,
//                    rescheduledTimeStart = DatetimeAppManager(currentDate.plusDays(1), true).dateISO8601inString,
//                    rescheduledTimeEnd = DatetimeAppManager(endDateZone, true).dateISO8601inString,
//                )
//
//                taskViewModel.update(taskLeft)
//                val centerTaskId = taskViewModel.insert(taskCenter)
//                val rightTaskId = taskViewModel.insert(taskRight)
//
//                result = migrateNotes(taskId, centerTaskId, rightTaskId)
//            }
//
//            if (!result) {
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(requireContext(), "Reschedule Failed", Toast.LENGTH_SHORT).show()
//                }
//            } else {
//                closeAction.start()
//            }
//
//        }
//    }
//
//    private suspend fun migrateNotes(taskId: Long, centerTaskId : Long?, rightTaskId: Long) : Boolean {
//        return withContext(Dispatchers.IO) {
//            // TODO: Migrate Notes
//            val defLeftTask = async(Dispatchers.IO) {
//                taskViewModel.getById(taskId)
//            }
//
//            val defCenterTask = async(Dispatchers.IO) {
//                if (centerTaskId != null) {
//                    taskViewModel.getById(centerTaskId)
//                } else {
//                    null
//                }
//            }
//
//            val defRightTask = async(Dispatchers.IO) {
//                taskViewModel.getById(rightTaskId)
//            }
//
//
//            val defQuizes = async(Dispatchers.IO) {
//                notesViewModel.note.getByFKTaskIdAndCategoryAsList(
//                    taskId,
//                    CATEGORY_QUIZ
//                )
//            }
//
//            val defToPacks = async(Dispatchers.IO) {
//                notesViewModel.note.getByFKTaskIdAndCategoryAsList(
//                    taskId,
//                    CATEGORY_TO_PACK
//                )
//            }
//
//            val defMemos = async(Dispatchers.IO) {
//                notesViewModel.note.getByFKTaskIdAndCategoryAsList(
//                    taskId,
//                    CATEGORY_MEMO
//                )
//            }
//
//            val leftTask: TaskTable
//            val centerTask: TaskTable?
//            val rightTask : TaskTable
//            val quizes: List<NotesTaskTable>?
//            val toPacks: List<NotesTaskTable>?
//            val memos: List<NotesTaskTable>?
//
//            awaitAll(
//                defLeftTask,
//                defCenterTask,
//                defRightTask,
//                defQuizes,
//                defToPacks,
//                defMemos
//            ).let {
//                leftTask = it[0] as TaskTable
//                centerTask = it[1] as TaskTable?
//                rightTask = it[2] as TaskTable
//                quizes = it[3] as List<NotesTaskTable>?
//                toPacks = it[4] as List<NotesTaskTable>?
//                memos = it[5] as List<NotesTaskTable>?
//            }
//
//            val leftTaskEndTime =
//                DatetimeAppManager(leftTask.rescheduledTimeEnd!!).selectedDetailDatetimeISO
//
//            val centerTaskEndTime = centerTaskId?.let {
//                DatetimeAppManager(centerTask!!.rescheduledTimeEnd!!).selectedDetailDatetimeISO
//            }
//
//            val newDayAdaptInterval = rightTask.indexDay - leftTask.indexDay
//
//
//            val jobList = mutableListOf<Deferred<Unit>>()
//
//            if (quizes != null) {
//                for (quiz in quizes) {
//                    val currDate = DatetimeAppManager(quiz.dateISO8601).selectedDetailDatetimeISO
//                    if (currDate.isBefore(leftTaskEndTime) || currDate.isEqual(leftTaskEndTime)) {
//                        continue
//                    } else if (centerTaskId != null && currDate.isBefore(centerTaskEndTime!!) || currDate.isEqual(
//                            centerTaskEndTime!!
//                        )
//                    ) {
//                        quiz.fkTaskId = centerTaskId
//                        quiz.dateISO8601 = DatetimeAppManager(centerTaskEndTime).dateISO8601inString
//                    } else {
//                        quiz.fkTaskId = rightTaskId
//                        quiz.dateISO8601 = DatetimeAppManager(currDate.plusDays(newDayAdaptInterval.toLong())).dateISO8601inString
//                    }
//
//                    val job = async(Dispatchers.IO) {
//                        notesViewModel.note.update(quiz)
//                    }
//
//                    jobList.add(job)
//                }
//            }
//
//            if (toPacks != null) {
//                for (toPack in toPacks) {
//                    val currDate = DatetimeAppManager(toPack.dateISO8601).selectedDetailDatetimeISO
//                    if (currDate.isBefore(leftTaskEndTime) || currDate.isEqual(leftTaskEndTime)) {
//                        continue
//                    } else if (centerTaskId != null && currDate.isBefore(centerTaskEndTime!!) || currDate.isEqual(
//                            centerTaskEndTime!!
//                        )
//                    ) {
//                        toPack.fkTaskId = centerTaskId
//                        toPack.dateISO8601 = DatetimeAppManager(centerTaskEndTime).dateISO8601inString
//                    } else {
//                        toPack.fkTaskId = rightTaskId
//                        toPack.dateISO8601 = DatetimeAppManager(currDate.plusDays(newDayAdaptInterval.toLong())).dateISO8601inString
//                    }
//
//                    val job = async(Dispatchers.IO) {
//                        notesViewModel.note.update(toPack)
//                    }
//
//                    jobList.add(job)
//                }
//            }
//
//            if (memos != null) {
//                for (memo in memos) {
//                    val currDate = DatetimeAppManager(memo.dateISO8601).selectedDetailDatetimeISO
//                    if (currDate.isBefore(leftTaskEndTime) || currDate.isEqual(leftTaskEndTime)) {
//                        continue
//                    } else if (centerTaskId != null && currDate.isBefore(centerTaskEndTime!!) || currDate.isEqual(
//                            centerTaskEndTime!!
//                        )
//                    ) {
//                        memo.fkTaskId = centerTaskId
//                        memo.dateISO8601 = DatetimeAppManager(centerTaskEndTime).dateISO8601inString
//                    } else {
//                        memo.fkTaskId = rightTaskId
//                        memo.dateISO8601 = DatetimeAppManager(currDate.plusDays(newDayAdaptInterval.toLong())).dateISO8601inString
//                    }
//
//                    val job = async(Dispatchers.IO) {
//                        notesViewModel.note.update(memo)
//                    }
//
//                    jobList.add(job)
//                }
//            }
//
//            jobList.awaitAll()
//            true
//        }
//    }
//
//    private fun validateForm(
//        indexDay: Int?,
//        customSession: Boolean,
//        customTimeStart: String?,
//        customTimeEnd: String?
//    ): Boolean {
//        if (indexDay == null) {
//            val errorMsg = getString(R.string.error_no_day_selected)
//            binding.textInputLayoutSelectSession.error = errorMsg
//            return false
//        }
//
//        if (!customSession) return true
//
//        if (customTimeStart == null || customTimeStart == "") {
//            val errorMsg = getString(R.string.error_msg_time_is_not_valid)
//            binding.textInputLayoutStartTime.error = errorMsg
//            return false
//        }
//
//        if (customTimeEnd == null || customTimeEnd == "") {
//            val errorMsg = getString(R.string.error_msg_time_is_not_valid)
//            binding.textInputLayoutEndTime.error = errorMsg
//            return false
//        }
//
//        return true
//    }
//
//
//    private fun setupSwitch() {
//        binding.switchRescheduleOption.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                binding.rescheudleOption.text = getString(R.string.reschedule_all_following_meet)
//            } else {
//                binding.rescheudleOption.text = getString(R.string.reschedule_option_only_this_time)
//            }
//        }
//    }
//
//
//    private fun setSessionDropDown() {
//        CustomTextField(requireContext()).setDropdownTextField(
//            binding.textInputLayoutSelectSession,
//            onClick = spinnerSelectSessionOnClick()
//        )
//
//        rescheduleFormViewModel.session.observe(viewLifecycleOwner) {
//            rescheduleFormViewModel.setIsCustomSession(false)
//            if (it != null) {
//                val text = "${it.timeStart} - ${it.timeEnd}"
//                binding.textInputLayoutSelectSession.editText?.setText(text)
//                clearSessionError()
//            } else {
//                binding.textInputLayoutSelectSession.editText?.setText("")
//            }
//        }
//    }
//
//    private fun clearSessionError() {
//        binding.textInputLayoutSelectSession.error = null
//        binding.textInputLayoutSelectSession.isErrorEnabled = false
//    }
//
//    private fun spinnerSelectSessionOnClick(): () -> Unit = {
//        lifecycleScope.launch {
//            Log.d("RescheduleFragment", "spinnerSelectSessionOnClicked, isDialogOpen: ${MinimumBottomSheetDialog.isDialogOpen}")
//
//            if (MinimumBottomSheetDialog.isDialogOpen) return@launch
//
//
//            if (rescheduleFormViewModel.getDay() == null) {
//                Log.e("RescheduleFragment", "spinnerSelectSessionOnClick: day is null")
//                val errorMsg = getString(R.string.error_no_day_selected)
//                binding.textInputLayoutSelectSession.error = errorMsg
//                return@launch
//            }
//
//            val selectedDayId = rescheduleFormViewModel.getDay()!!.id
//
//            val sessionsFiltered = withContext(Dispatchers.IO) {
//                sessionViewModel.getSessionsForRoutine(
//                    args.routineId,
//                    isCustomSessionIncluded = false
//                )
//            }.filter { session ->
//                session.selectedDays[selectedDayId.toInt()] == '1'
//            }
//
//            val sessionsAdapter = GlobalAdapter(
//                useIndexes = true,
//                useCustomName = true,
//                customNameLogic = { session ->
//                    if (session is SessionTable) {
//                        return@GlobalAdapter "${session.timeStart} - ${session.timeEnd}"
//                    } else {
//                        Log.wtf("RescheduleFragment", "onClickSessionItem: it is not SessionTable")
//                        return@GlobalAdapter session.title
//                    }
//                }
//            )
//            val bottomSheet = GlobalBottomSheetDialogFragment(
//                title = getString(R.string.default_select_session),
//                globalAdapter = sessionsAdapter,
//                useAdditionalButton = true,
//                additionalButtonText = getString(R.string.custom_session),
//                additionalButtonLogic = customSessionLogic()
//            )
//            sessionsAdapter.onClickAction = onClickSessionItem(bottomSheet)
//
//            sessionsAdapter.submitList(sessionsFiltered)
//
//            bottomSheet.show(parentFragmentManager, "select_session_bottom_sheet")
//
//        }
//    }
//
//    private fun onClickSessionItem(bottomSheet: GlobalBottomSheetDialogFragment): (GlobalMinimumInterface) -> Unit = {
//        rescheduleFormViewModel.setIsCustomSession(false)
//        if (it is SessionTable) {
//            rescheduleFormViewModel.setSession(it)
//        } else {
//            Log.wtf("RescheduleFragment", "onClickSessionItem: it is not SessionTable")
//        }
//        bottomSheet.closeDialog()
//    }
//
//    private fun customSessionLogic(): () -> Unit = {
//        rescheduleFormViewModel.setSession(null)
//        rescheduleFormViewModel.setIsCustomSession(true)
//        val text = getString(R.string.custom_session)
//        binding.textInputLayoutSelectSession.editText?.setText(text)
//    }
//
//
//    private fun setupRescheduleDatePicker() {
//        DatetimeAppManager().setEditTextDatePickerDialog(
//            requireContext(),
//            parentFragmentManager,
//            binding.textInputLayoutAlternativeReschedule,
//            forwardOnly = true,
//            customSuccessAction = {
//                val selectedDate = binding.textInputLayoutAlternativeReschedule.editText?.text.toString()
//                val iso8601 = DatetimeAppManager().convertReadableDateToIso8601(selectedDate)
//                val datetimeManager = DatetimeAppManager(iso8601)
//                val daysOfWeekImpl = DaysOfWeekImpl(
//                    id = datetimeManager.getTodayDayId().toLong(),
//                    title = datetimeManager.getTodayDayName()
//                )
//                rescheduleFormViewModel.setDay(daysOfWeekImpl)
//                clearSessionError()
//            }
//        )
//    }
//
//    private fun setupSelectedDatePicker() {
//        lifecycleScope.launch {
//            Log.d("RescheduleFragment", "setupSelectedDatePicker: program reached")
//
//            val routine = withContext(Dispatchers.IO){ routineViewModel.getById(args.routineId) } ?: return@launch
//            val dateEnd = DatetimeAppManager(routine.date_end).selectedDetailDatetimeISO
//
//            val currentDate = args.selectedDatetimeDetailIso.zoneDateTime
//
//            Log.d("RescheduleFragment", "setupSelectedDatePicker: $currentDate and $dateEnd")
//
//            if (currentDate.isAfter(dateEnd)) return@launch
//
//            val weeks = ChronoUnit.WEEKS.between(currentDate, dateEnd).toInt()
//
//            for (i in 0..weeks) {
//                val dateTime = DatetimeAppManager(currentDate.plusWeeks(i.toLong())).toReadable()
//                if (i == 1) {
//                    weeksDictionary["Next week | $dateTime"] = i
//                    continue
//                } else if (i == 0) {
//                    weeksDictionary["This week | $dateTime"] = 0
//                    continue
//                }
//                weeksDictionary[ "In $i weeks | $dateTime"] = i
//            }
//
//            val adapter: ArrayAdapter<String> = object : ArrayAdapter<String>(
//                requireContext(),
//                android.R.layout.simple_dropdown_item_1line,
//                weeksDictionary.keys.toTypedArray()
//            ) {
//                override fun getFilter(): Filter {
//                    return object : Filter() {
//                        override fun performFiltering(constraint: CharSequence?): FilterResults {
//                            return FilterResults().apply { values = weeksDictionary.keys.toTypedArray(); count = weeksDictionary.keys.size }
//                        }
//
//                        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
//                            if (results != null && results.count > 0) {
//                                notifyDataSetChanged()
//                            } else {
//                                notifyDataSetInvalidated()
//                            }
//                        }
//                    }
//                }
//            }
//
//
//            (binding.textInputLayoutSelectedReschedule.editText
//                    as? MaterialAutoCompleteTextView
//                    )?.setAdapter(adapter)
//
//            binding.textInputLayoutSelectedReschedule.editText?.apply {
//                isFocusable = false
//                isClickable = true
//                isCursorVisible = false
//            }
//
//
//            binding.textInputLayoutSelectedReschedule.editText?.setText(weeksDictionary.keys.first())
//
//            Log.d("RescheduleFragment", "setupSelectedDatePicker: $weeksDictionary")
//        }
//    }
//
//    private fun setupStartTimePicker() {
//        DatetimeAppManager().setEditTextTimePickerDialog(
//            requireContext(),
//            parentFragmentManager,
//            binding.textInputLayoutStartTime,
//            customSuccessAction = {
//                val startTime = binding.textInputLayoutStartTime.editText?.text.toString()
//                rescheduleFormViewModel.setTimeStart(startTime)
//            }
//
//        )
//
//        DatetimeAppManager().setEditTextTimePickerDialog(
//            requireContext(),
//            parentFragmentManager,
//            binding.textInputLayoutEndTime,
//            customSuccessAction = {
//                val endTime = binding.textInputLayoutEndTime.editText?.text.toString()
//                rescheduleFormViewModel.setTimeEnd(endTime)
//            }
//        )
//
//        setCustomSessionObserver()
//    }
//
//    private fun setCustomSessionObserver() {
//        rescheduleFormViewModel.isCustomSession.observe(viewLifecycleOwner) { isCustomeSession ->
//            if (isCustomeSession) {
//                binding.groupCustomSession.visibility = View.VISIBLE
//
//                if (rescheduleFormViewModel.isObserverActive) return@observe
//                rescheduleFormViewModel.isObserverActive = true
//
//                rescheduleFormViewModel.timeStart.observe(viewLifecycleOwner) {
//                    it?.let {
//                        lifecycleScope.launch {
//                            val isActive = rescheduleFormViewModel.getIsCustomSession()
//
//                            if (isActive) {
//                                val timeEnd = rescheduleFormViewModel.getTimeEnd()
//
//                                if (timeEnd == null || timeEnd == "") return@launch
//
//                                checkTimeInterval(it, timeEnd)
//                            }
//
//                        }
//                    }
//
//                }
//
//                rescheduleFormViewModel.timeEnd.observe(viewLifecycleOwner) {
//                    it?.let {
//                        lifecycleScope.launch {
//                            val isActive = rescheduleFormViewModel.getIsCustomSession()
//
//                            if (isActive) {
//                                val timeStart = rescheduleFormViewModel.getTimeStart()
//
//                                if(timeStart == null || timeStart == "") return@launch
//
//                                checkTimeInterval(timeStart, it)
//                            }
//
//                        }
//                    }
//                }
//            } else {
//                binding.groupCustomSession.visibility = View.GONE
//
//                if (rescheduleFormViewModel.isObserverActive) {
//                    rescheduleFormViewModel.timeStart.removeObservers(viewLifecycleOwner)
//                    rescheduleFormViewModel.timeEnd.removeObservers(viewLifecycleOwner)
//                    rescheduleFormViewModel.isObserverActive = false
//                }
//            }
//        }
//
//    }
//
//    private fun checkTimeInterval(timeStart: String, timeEnd: String) {
//        val startTime = DatetimeAppManager().convertStringTimeToMinutes(timeStart)
//        val endTime = DatetimeAppManager().convertStringTimeToMinutes(timeEnd)
//
//        if (startTime >= endTime) {
//            val errorMsg = getString(R.string.error_time_interval)
//            binding.textInputLayoutEndTime.error = errorMsg
//        } else {
//            binding.textInputLayoutEndTime.error = null
//        }
//    }
//
//    private fun setupErrorMessages() {
//        ErrorMsgManager().setErrorMessages(
//            requireContext(),
//            binding.teksInputLayoutLocation,
//            binding.teksInputLayoutLocation.editText,
//            "20 characters max"
//        )
//    }

}