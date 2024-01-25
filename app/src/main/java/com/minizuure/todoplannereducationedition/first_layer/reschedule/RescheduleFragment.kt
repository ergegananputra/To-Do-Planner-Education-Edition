package com.minizuure.todoplannereducationedition.first_layer.reschedule

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Toast
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
import com.minizuure.todoplannereducationedition.services.database.CATEGORY_MEMO
import com.minizuure.todoplannereducationedition.services.database.CATEGORY_QUIZ
import com.minizuure.todoplannereducationedition.services.database.CATEGORY_TO_PACK
import com.minizuure.todoplannereducationedition.services.database.DEFAULT_SESSION_ID
import com.minizuure.todoplannereducationedition.services.database.notes.NoteViewModel
import com.minizuure.todoplannereducationedition.services.database.notes.NoteViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.notes.NotesTaskTable
import com.minizuure.todoplannereducationedition.services.database.relations_table.SessionTaskProviderTable
import com.minizuure.todoplannereducationedition.services.database.relations_table.SessionTaskProviderViewModel
import com.minizuure.todoplannereducationedition.services.database.relations_table.SessionTaskProviderViewModelFactory
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
import com.minizuure.todoplannereducationedition.services.notification.ItemAlarmQueue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

/**
 * list TODO in [RescheduleFragment]:
 *
 *
 * [ ] Perbaiki logic pendapatan week pada methods [setupSelectedDatePicker] untuk menyesuaikan [SessionTaskProvider].
 *       Solution Idea : Dapat menggunakan crawler terdekat untuk menentukan week yang akan ditampilkan.
 *
 *
 * see [TaskDetailBottomSheetDialogFragment] for comparison.
 */
class RescheduleFragment : Fragment() {
    val args : RescheduleFragmentArgs by navArgs()
    private val rescheduleFormViewModel : RescheduleFormViewModel by activityViewModels()
    private lateinit var routineViewModel : RoutineViewModel
    private lateinit var sessionViewModel: SessionViewModel
    private lateinit var taskViewModel : TaskViewModel
    private lateinit var notesViewModel : NoteViewModel
    private lateinit var sessionTaskProviderViewModel : SessionTaskProviderViewModel

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

        setupSelectedDatePicker()
        setupRescheduleDatePicker()
        setupStartTimePicker()
        setSessionDropDown()
        setupSwitch()

        setupSaveButton()
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

        val noteFactory = NoteViewModelFactory(app.appRepository)
        notesViewModel = ViewModelProvider(requireActivity(), noteFactory)[NoteViewModel::class.java]

        val sessionTaskProviderFactory = SessionTaskProviderViewModelFactory(app.appRepository)
        sessionTaskProviderViewModel = ViewModelProvider(requireActivity(), sessionTaskProviderFactory)[SessionTaskProviderViewModel::class.java]
    }

    private fun setupSaveButton() {
        /**
         * TODO: membuat save button logic.
         * 1.   Cek Switch, jika true maka reschedule semua task
         *      yang ada di session tersebut setelah hari Task yang dipilih.
         *
         * 2.   Buat Task Duplicate dengan rentang waktu yang dipilih.
         *
         * 3.   Migrasi semua notes setelah hari Task yang dipilih ke Task Duplicate.
         *
         */

        binding.buttonSaveRescheduleForm.setOnClickListener {
            Log.d("RescheduleFragment", "setupSaveButton: clicked")
            Toast.makeText(requireContext(), "Clicked: Unimplemented yet", Toast.LENGTH_SHORT).show()

            lifecycleScope.launch(Dispatchers.IO) {
                val taskId : Long = args.taskId
                val weekFromNow : Int = weeksDictionary[binding.textInputLayoutSelectedReschedule.editText?.text.toString()] ?: 0
                val breakPointDate : String = binding.textInputLayoutAlternativeReschedule.editText?.text.toString().trim()
                val indexDay : Int? = rescheduleFormViewModel.getDay()?.getId()
                val sessionId : Long = rescheduleFormViewModel.getSession()?.id ?: DEFAULT_SESSION_ID
                val isCustomSession : Boolean = rescheduleFormViewModel.getIsCustomSession()
                val customTimeStart : String? = rescheduleFormViewModel.getTimeStart()
                val customTimeEnd : String? = rescheduleFormViewModel.getTimeEnd()
                val location : String = binding.teksInputLayoutLocation.editText?.text.toString().trim()
                val locationLink : String = binding.textInputLayoutMapsLink.editText?.text.toString().trim()
                val isRescheduleAllFollowingMeet : Boolean = binding.switchRescheduleOption.isChecked
                // TODO: Set Community ID

                val isValid = withContext(Dispatchers.Main) {
                    validateForm(
                        indexDay,
                        isCustomSession,
                        customTimeStart,
                        customTimeEnd
                    )
                }


                if (!isValid) return@launch

                rescheduleTask(
                    taskId,
                    weekFromNow,
                    DatetimeAppManager().convertReadableDateToIso8601(breakPointDate),
                    indexDay!!,
                    sessionId,
                    isCustomSession,
                    customTimeStart,
                    customTimeEnd,
                    location,
                    locationLink,
                    isRescheduleAllFollowingMeet,
                    this.launch(Dispatchers.Main) {
                        (requireActivity() as TaskManagementActivity).onSupportNavigateUp()
                    }
                )


            }
        }
    }

    /**
     *
     * [rescheduleTask] is used to reschedule a task.
     *
     *
     * Rescheduling a task ALWAYS create a new Session IF [SessionTaskProviderTable]
     * already exist with same [indexDay], [taskId] and [sessionId].
     *
     *
     * Last Edit : 25 January 2024
     *
     *
     * Note : Please update the javaDoc if you edit this function.
     */
    private suspend fun rescheduleTask(
        taskId: Long,
        weekFromNow: Int,
        alternateDateString: String,
        indexDay: Int,
        sessionId: Long,

        isCustomSession: Boolean,
        customTimeStart: String?,
        customTimeEnd: String?,

        location: String,
        locationLink: String,
        isRescheduleAllFollowingMeet: Boolean,

        closeAction: Job
    ) {
        withContext(Dispatchers.IO) {
            val taskAndSessionJoin = taskViewModel.getTaskAndSessionJoinByProviderPrimaryKeys(
                indexDay = args.indexDay,
                taskId = args.taskId,
                sessionId = args.sessionId
            ) ?: return@withContext

            val routine = routineViewModel.getById(args.routineId) ?: return@withContext

            val alternateDate = DatetimeAppManager(DatetimeAppManager(alternateDateString).selectedDetailDatetimeISO, accuracyOnlyUpToDays = true)
            val currentDate = args.selectedDatetimeDetailIso.zoneDateTime.plusWeeks(weekFromNow.toLong())

            val referenceSessionTaskProvider = sessionTaskProviderViewModel.getByPrimaryKeys(
                indexDay = args.indexDay,
                taskId = args.taskId,
                sessionId = args.sessionId
            ) ?: return@withContext

            val sessionTaskProvider = sessionTaskProviderViewModel.getByPrimaryKeys(
                indexDay = indexDay,
                taskId = taskId,
                sessionId = sessionId
            )

            val alterSessionId = if (sessionTaskProvider != null) {
                // create copy of session
                val oldSession = sessionViewModel.getById(sessionId) ?: return@withContext

                sessionViewModel.insert(
                    oldSession.copy(
                        id = 0,
                        title = "Reschedule Session $sessionId from Task $taskId",
                        selectedDays = booleanSelectedDaysGenerator(indexDay),
                        isCustomSession = true
                    )
                )
            } else if(sessionId == DEFAULT_SESSION_ID && isCustomSession) {
                sessionViewModel.insert(
                    title = "Reschedule Session $sessionId from Task $taskId",
                    timeStart = customTimeStart!!,
                    timeEnd = customTimeEnd!!,
                    selectedDays = booleanSelectedDaysGenerator(indexDay),
                    fkRoutineId = taskAndSessionJoin.sessionFkRoutineId,
                    isCustomSession = true
                )
            } else {
                sessionId
            }

            if (isRescheduleAllFollowingMeet) {
                // Break to 2 Segment

                // Segment Left (Update)
                sessionTaskProviderViewModel.update(
                    referenceSessionTaskProvider.copy(
                        isRescheduled = true,
                        rescheduledDateStart = if(referenceSessionTaskProvider.isRescheduled) referenceSessionTaskProvider.rescheduledDateStart  else routine.date_start,
                        rescheduledDateEnd = DatetimeAppManager(currentDate).dateISO8601inString,
                    )
                )

                // Segment Right (Insert)
                sessionTaskProviderViewModel.insert(
                    SessionTaskProviderTable(
                        fkTaskId = args.taskId,
                        indexDay = indexDay,
                        fkSessionId = alterSessionId,

                        locationName = location,
                        locationLink = locationLink,

                        isRescheduled = true,
                        rescheduledDateStart = DatetimeAppManager(currentDate.plusDays(1)).dateISO8601inString,
                        rescheduledDateEnd = routine.date_end,
                    )
                )

                migrateAllNotes(
                    alternateDate,
                    currentDate,
                    2
                )

            } else {
                // Break to 3 Segment

                // Segment Left (Update)
                sessionTaskProviderViewModel.update(
                    referenceSessionTaskProvider.copy(
                        isRescheduled = true,
                        rescheduledDateStart = if(referenceSessionTaskProvider.isRescheduled) referenceSessionTaskProvider.rescheduledDateStart  else routine.date_start,
                        rescheduledDateEnd = DatetimeAppManager(currentDate.minusDays(1)).dateISO8601inString,
                    )
                )

                // Segment Center (Insert)
                sessionTaskProviderViewModel.insert(
                    SessionTaskProviderTable(
                        fkTaskId = args.taskId,
                        indexDay = indexDay,
                        fkSessionId = alterSessionId,

                        locationName = location,
                        locationLink = locationLink,

                        isRescheduled = true,
                        rescheduledDateStart = alternateDate.dateISO8601inString,
                        rescheduledDateEnd = alternateDate.dateISO8601inString,
                    )
                )

                // Segment Right (Insert)
                val copyOldSession = sessionViewModel.getById(sessionId)!!
                val newSessionId = sessionViewModel.insert(
                    copyOldSession.copy(
                        id = 0,
                        title = "Reschedule Session $sessionId from Task $taskId",
                        selectedDays = booleanSelectedDaysGenerator(indexDay),
                        isCustomSession = true
                    )
                )

                sessionTaskProviderViewModel.insert(
                    referenceSessionTaskProvider.copy(
                        fkSessionId = newSessionId,

                        isRescheduled = true,
                        rescheduledDateStart = DatetimeAppManager(currentDate.plusDays(1)).dateISO8601inString,
                        rescheduledDateEnd = routine.date_end,
                    )
                )

                migrateAllNotes(
                    alternateDate,
                    currentDate,
                    3
                )

            }

            closeAction.start()
        }
    }

    private fun booleanSelectedDaysGenerator(indexDay: Int)
    = "0000000".mapIndexed { index, _ ->
            if (index == indexDay) '1' else '0'
        }.joinToString(separator = "")




    private suspend fun migrateAllNotes(
        alternateDate: DatetimeAppManager,
        currentDate: ZonedDateTime,
        segment: Int
    ) {
        withContext(Dispatchers.IO) {
            notesViewModel.note.getByFKTaskIdAndCategoryAsList(
                fkTaskId = args.taskId,
                category = CATEGORY_QUIZ
            ).let {
                if (it != null) migrateNotes(
                    it,
                    alternateDate,
                    currentDate,
                    segment
                )
            }

            notesViewModel.note.getByFKTaskIdAndCategoryAsList(
                fkTaskId = args.taskId,
                category = CATEGORY_TO_PACK
            ).let {
                if (it != null) migrateNotes(
                    it,
                    alternateDate,
                    currentDate,
                    segment
                )
            }

            notesViewModel.note.getByFKTaskIdAndCategoryAsList(
                fkTaskId = args.taskId,
                category = CATEGORY_MEMO
            ).let {
                if (it != null) migrateNotes(
                    it,
                    alternateDate,
                    currentDate,
                    segment
                )
            }
        }
    }

    private suspend fun migrateNotes(
        notesTaskTables: List<NotesTaskTable>,
        alternateDate: DatetimeAppManager,
        currentDate: ZonedDateTime,
        segment: Int
    ) {
        val app = requireActivity().application as ToDoPlannerApplication
        val alarmManager = app.appAlarmManager

        withContext(Dispatchers.IO) {
            val alternateDayIndex = alternateDate.getTodayDayId()
            val currentDayIndex = DatetimeAppManager(currentDate).getTodayDayId()
            val intervalDay = alternateDayIndex - currentDayIndex

            notesTaskTables.forEach {
                val notesDate = DatetimeAppManager(it.dateISO8601).selectedDetailDatetimeISO
                val isEdited = if (notesDate.isEqual(currentDate) && segment == 3) {
                    Log.d("RescheduleFragment", "migrateNotes center Triggered: ${it.dateISO8601} to ${alternateDate.dateISO8601inString}")
                    notesViewModel.note.update(
                        it.copy(
                            dateISO8601 = alternateDate.dateISO8601inString
                        )
                    )
                    alternateDate

                } else if (notesDate.isAfter(currentDate) && segment == 2) {
                    val editedDate = DatetimeAppManager(
                        DatetimeAppManager(it.dateISO8601).selectedDetailDatetimeISO.plusDays(intervalDay.toLong())
                    )
                    notesViewModel.note.update(
                        it.copy(
                            dateISO8601 = editedDate.dateISO8601inString
                        )
                    )

                    editedDate
                } else {
                    null
                }

                if (isEdited != null) {
                    Log.d("RescheduleFragment", "migrateNotes: ${it.dateISO8601} to ${alternateDate.dateISO8601inString} \n " +
                            "Run update notification schedule")

                    val notificationId = ItemAlarmQueue().createItemId(
                        isEdited.selectedDetailDatetimeISO.dayOfMonth,
                        isEdited.selectedDetailDatetimeISO.monthValue,
                        isEdited.selectedDetailDatetimeISO.year,
                        args.taskId
                    )

                    val cancelledDatetime = DatetimeAppManager(it.dateISO8601).selectedDetailDatetimeISO

                    val itemAlarmQueueCancelled = ItemAlarmQueue(
                        id = notificationId,
                        action = it.category,
                        taskId = args.taskId,
                        time = cancelledDatetime.withHour(5).withMinute(30),
                        taskName = args.taskTitle,
                        message = it.description,
                        taskDateIdentification = cancelledDatetime
                    )

                    alarmManager.cancel(
                        itemAlarmQueueCancelled
                    )

                    val itemAlarmQueue = itemAlarmQueueCancelled.copy(
                        time = isEdited.selectedDetailDatetimeISO.withHour(5).withMinute(30),
                        taskDateIdentification = isEdited.selectedDetailDatetimeISO
                    )

                    alarmManager.schedule(
                        itemAlarmQueue
                    )
                }
            }
        }
    }

    private fun validateForm(
        indexDay: Int?,
        customSession: Boolean,
        customTimeStart: String?,
        customTimeEnd: String?
    ): Boolean {
        if (indexDay == null) {
            val errorMsg = getString(R.string.error_no_day_selected)
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

        return true
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
        // TODO: Perbaiki logic untuk mendapatkan list week yang valid. Karena implementasi saat ini tidak dapat
        //  menangani kasus SessionTaskProvider yang sudah direschedule di beda tanggal / hari
        lifecycleScope.launch {
            Log.d("RescheduleFragment", "setupSelectedDatePicker: program reached")

            val routine = withContext(Dispatchers.IO){ routineViewModel.getById(args.routineId) } ?: return@launch
            val dateEnd = DatetimeAppManager(routine.date_end).selectedDetailDatetimeISO

            val currentDate = args.selectedDatetimeDetailIso.zoneDateTime

            Log.d("RescheduleFragment", "setupSelectedDatePicker: $currentDate and $dateEnd")

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


            (binding.textInputLayoutSelectedReschedule.editText
                    as? MaterialAutoCompleteTextView
                    )?.setAdapter(adapter)

            binding.textInputLayoutSelectedReschedule.editText?.apply {
                isFocusable = false
                isClickable = true
                isCursorVisible = false
            }


            binding.textInputLayoutSelectedReschedule.editText?.setText(weeksDictionary.keys.first())

            Log.d("RescheduleFragment", "setupSelectedDatePicker: $weeksDictionary")
        }
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